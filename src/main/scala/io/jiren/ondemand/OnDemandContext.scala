package io.jiren.ondemand

import java.net.InetSocketAddress

/** A context full of dependencies for the [[OnDemandService]].
  * @author Sino
  */
case class OnDemandContext(
  address: InetSocketAddress,
  workerLimit: Int,
  clientRev: Int,
  assetLoader: AssetLoader
)
