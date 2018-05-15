package io.jiren.ondemand.client

import akka.actor.{Actor, ActorRef, Props}
import io.jiren.tcp.TcpListener.{ClientConnected, RegisterHandler}

/** A companion object to [[ClientMaster]]. */
object ClientMaster {
  def props(workerMaster: ActorRef) =
    Props(new ClientMaster(workerMaster))
}

/** Supervises over [[Client]] actors.
  * @author Sino
  */
final class ClientMaster(workerMaster: ActorRef) extends Actor {
  override def receive = {
    case ClientConnected(connection, remote) =>
      val tcp = sender()
      val client = context.actorOf(Client.props(connection, workerMaster))

      tcp ! RegisterHandler(connection, client)
  }
}
