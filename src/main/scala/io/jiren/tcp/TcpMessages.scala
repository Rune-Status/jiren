package io.jiren.tcp

import akka.io.Tcp

/** @author Sino */
object TcpMessages {
  case object Ack extends Tcp.Event
}
