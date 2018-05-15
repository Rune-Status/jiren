package io.jiren.tcp

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import io.jiren.tcp.TcpListener._

/** A companion object to [[TcpListener]]. */
object TcpListener {
  case class StartListening(address: InetSocketAddress)
  case object StopListening

  case object StartedListening
  case object StoppedListening

  case class ClientConnected(connection: ActorRef, remote: InetSocketAddress)
  case class RegisterHandler(connection: ActorRef, handler: ActorRef)

  def props(owner: ActorRef, clientMaster: ActorRef) =
    Props(new TcpListener(owner, clientMaster))
}

/** @author Sino */
final class TcpListener(owner: ActorRef, clientMaster: ActorRef) extends Actor {
  override def receive =
    unbound

  def unbound: Receive = {
    case StartListening(address) =>
      IO(Tcp)(context.system) ! Bind(self, address)

    case Bound(localAddr) =>
      owner ! StartedListening
      context.become(bound(sender()))
  }

  def bound(connectionManager: ActorRef): Receive = {
    case Connected(remote, local) =>
      clientMaster ! ClientConnected(sender(), remote)

    case RegisterHandler(connection, handler) =>
      connection ! Register(handler)

    case StopListening =>
      connectionManager ! Unbind

    case Unbound =>
      owner ! StoppedListening
      context.become(unbound)
  }
}
