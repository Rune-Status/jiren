package io.jiren

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import io.jiren.ondemand.{AssetLoader, OnDemandContext, OnDemandService}

/** @author Sino */
object Jiren {
  def create(name: String, port: Int, workerLimit: Int, clientRev: Int, loader: AssetLoader): Unit =
    create(ActorSystem(s"JirenFor-$name"), port, workerLimit, clientRev: Int, loader)

  def create(system: ActorSystem, port: Int, workerLimit: Int, clientRev: Int, loader: AssetLoader): Unit = {
    val address = new InetSocketAddress(port)
    val context = OnDemandContext(address, workerLimit, clientRev, loader)

    system.actorOf(OnDemandService.props(context), name = "ondemand")
  }
}
