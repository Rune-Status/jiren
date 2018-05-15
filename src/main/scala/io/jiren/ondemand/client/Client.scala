package io.jiren.ondemand.client

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.util.ByteString
import io.jiren.ondemand.Worker._
import io.jiren.ondemand.WorkerMaster.{FetchWorker, ReachedWorkerLimit, WorkerFetch}
import io.jiren.tcp.TcpMessages.Ack
import io.jiren.util.ByteBufferExtensions._
import io.jiren.util.ResponseCodes

import scala.collection.immutable

/** A companion object to [[Client]]. */
object Client {
  def props(connection: ActorRef, workerMaster: ActorRef) =
    Props(new Client(connection, workerMaster))
}

/** @author Sino */
final class Client(connection: ActorRef, workerMaster: ActorRef) extends Actor {
  var lastRead = ByteString.empty

  var pending = immutable.Seq.empty[ByteString]
  var flushing: Option[ByteString] = None

  override def receive =
    handshake

  def handshake: Receive = {
    case Received(data) =>
      val packet = lastRead ++ data
      if (packet.size < 5) {
        lastRead ++= data
      } else {
        val buffer = data.asByteBuffer

        val opcode = buffer.get() & 0xFF
        val revision = buffer.getInt()
        if (opcode == 15) {
          if (revision == 165) {
            workerMaster ! FetchWorker(self)

            lastRead = ByteString.empty
            context.become(awaitingWorker)
          } else {
            connection ! Write(ByteString(ResponseCodes.ClientUpdated))
            connection ! Close

            context stop self
          }
        } else {
          connection ! Close
          context stop self
        }
      }
  }

  def awaitingWorker: Receive = {
    case WorkerFetch(worker) =>
      flush(ByteString(ResponseCodes.MayProceed))
      context.become(servingAssets(worker, ByteBuffer.allocate(16384)) orElse writeBackpressure)

    case ReachedWorkerLimit =>
      connection ! Write(ByteString(ResponseCodes.ServerFull))
      connection ! Close

      context stop self

    case ErrorClosed(cause) =>
      context stop self

    case _: ConnectionClosed =>
      context stop self
  }

  def servingAssets(worker: ActorRef, out: ByteBuffer): Receive = {
    case Received(data) =>
      val packet = lastRead ++ data
      val in = packet.asByteBuffer

      var stop = false
      while (in.hasRemaining && !stop) {
        if (in.remaining() < 4) {
          // we require more bytes.. so we store the incomplete block of data
          // to have the remaining expected data concatenated to
          lastRead ++= ByteString(in.getBytes(in.remaining()))
          stop = true
        } else {
          val opcode = in.get() & 0xFF

          opcode match {
            case 0 | 1 =>
              val archive = in.get() & 0xFF
              val file = in.getShort()

              if (opcode == 1) {
                worker ! PrioritizedAssetRequest(archive, file)
              } else {
                worker ! RegularAssetRequest(archive, file)
              }

            case 2 | 3 =>
              in.skip(3)

              if (opcode == 2) {
                worker ! ClientLoggedIn
              } else {
                worker ! ClientLoggedOut
              }

            case 4 =>
              val encryptionValue = in.get() & 0xFF
              in.skip(2)
              worker ! ApplyEncryption(encryptionValue)
          }

          // if we've read all of the data, it means we can release the incomplete
          // block of data that we might have stored
          if (!in.hasRemaining) {
            lastRead = ByteString.empty
          }
        }
      }

    case ServeAsset(archive, file, setting, fileData) =>
      out.put(archive.toByte)
      out.putShort(file.toShort)
      out.put(setting.toByte)
      out.putInt(fileData.limit())
      out.put(fileData)

      flush(out.toByteString)

    case ErrorClosed(cause) =>
      context stop self

    case _: ConnectionClosed =>
      context stop self
  }

  def writeBackpressure: Receive = {
    case Ack =>
      flushing = None
      if (pending.nonEmpty) {
        connection ! Write(pending.head, Ack)

        flushing = Some(pending.head)
        pending = pending drop 1
      }
  }

  def flush(data: ByteString): Unit = {
    if (flushing.nonEmpty) {
      pending :+= data
    } else {
      connection ! Write(data, Ack)
      flushing = Some(data)
    }
  }
}
