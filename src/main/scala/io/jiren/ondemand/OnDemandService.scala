package io.jiren.ondemand

import akka.actor.{Actor, ActorLogging, Props}
import io.jiren.ondemand.client.ClientMaster
import io.jiren.tcp.TcpListener
import io.jiren.tcp.TcpListener.{StartListening, StartedListening, StoppedListening}

/** A companion object to [[OnDemandService]]. */
object OnDemandService {
  def props(ctx: OnDemandContext) =
    Props(new OnDemandService(ctx))
}

/** @author Sino */
final class OnDemandService(ctx: OnDemandContext) extends Actor with ActorLogging {
  val assetFileCache = context.actorOf(AssetFileCache.props(ctx.assetLoader), name = "assets-cache")
  val workerMaster = context.actorOf(WorkerMaster.props(ctx.workerLimit, assetFileCache), name = "worker-master")

  val clientMaster = context.actorOf(ClientMaster.props(workerMaster), name = "client-master")
  val tcpListener = context.actorOf(TcpListener.props(self, clientMaster), name = "tcp-listener")

  override def preStart() = {
    tcpListener ! StartListening(ctx.address)
  }

  override def receive = {
    case StartedListening =>
      log.info(s"Local channel bound at ${ctx.address}")

    case StoppedListening =>
      log.info("Local channel unbound")
  }
}
