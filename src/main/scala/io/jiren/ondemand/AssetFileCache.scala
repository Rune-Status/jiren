package io.jiren.ondemand

import java.nio.ByteBuffer

import akka.actor.{Actor, Props}
import io.jiren.ondemand.AssetFileCache.{Get, Result}

import scala.collection.mutable

/** A companion object to [[AssetFileCache]]. */
object AssetFileCache {
  case class Get(archive: Int, file: Int)
  case class Result(archive: Int, file: Int, data: ByteBuffer)

  def props(loader: AssetLoader) =
    Props(new AssetFileCache(loader))
}

/** A cache of previously loaded asset files. Asset files are loaded using the given
  * [[AssetLoader]].
  * @author Sino
  */
final class AssetFileCache(loader: AssetLoader) extends Actor {
  val assets = mutable.Map[Int, ByteBuffer]()

  override def receive = {
    case Get(archive, file) =>
      sender() ! Result(archive, file, assets
        .getOrElseUpdate(file << 16 | archive, loader.load(archive, file))
        .duplicate())
  }
}
