package io.jiren.ondemand

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef, Props, Terminated}
import io.jiren.ondemand.AssetFileCache.{Get, Result}
import io.jiren.ondemand.Worker._

/** A companion object to [[Worker]]. */
object Worker {
  case class PrioritizedAssetRequest(archive: Int, file: Int)
  case class RegularAssetRequest(archive: Int, file: Int)

  case object ClientLoggedIn
  case object ClientLoggedOut

  case class ApplyEncryption(value: Int)

  object ClientStates {
    case object LoggedIn extends Type
    case object LoggedOut extends Type
    sealed abstract class Type
  }

  case class ServeAsset(archive: Int, file: Int, setting: Int, fileData: ByteBuffer)

  def props(client: ActorRef, fileCache: ActorRef) =
    Props(new Worker(client, fileCache)).withMailbox("jiren.ondemand-worker-mailbox")
}

/** A dedicated file streaming worker that fetches asset files and serves them to the client.
  * @author Sino
  */
final class Worker(client: ActorRef, fileCache: ActorRef) extends Actor {
  var clientState: ClientStates.Type = _

  override def preStart() = {
    // signs a death pact with the client
    context.watch(client)
  }

  override def receive = {
    case PrioritizedAssetRequest(archive, file) =>
      fileCache ! Get(archive, file)

    case RegularAssetRequest(archive, file) =>
      fileCache ! Get(archive, file)

    case ClientLoggedIn =>
      clientState = ClientStates.LoggedIn

    case ClientLoggedOut =>
      clientState = ClientStates.LoggedOut

    case ApplyEncryption(value) =>
      // nothing

    case Result(archive, file, data) =>
      client ! ServeAsset(archive, file, setting = 0, data)

    case Terminated(`client`) =>
      context stop self
  }
}
