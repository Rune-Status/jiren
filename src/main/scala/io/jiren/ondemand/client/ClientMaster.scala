package io.jiren.ondemand.client

import akka.actor.{Actor, ActorRef, Props}
import io.jiren.tcp.TcpListener.{ClientConnected, RegisterHandler}

/** A companion object to [[ClientMaster]]. */
object ClientMaster {
  def props(workerMaster: ActorRef, clientRev: Int) =
    Props(new ClientMaster(workerMaster, clientRev))
}

/** Supervises over [[Client]] actors.
  * @author Sino
  */
final class ClientMaster(workerMaster: ActorRef, clientRev: Int) extends Actor {
  override def receive = {
    case ClientConnected(connection, remote) =>
      val tcp = sender()
      val client = context.actorOf(Client.props(connection, workerMaster, clientRev))

      tcp ! RegisterHandler(connection, client)
  }
}
