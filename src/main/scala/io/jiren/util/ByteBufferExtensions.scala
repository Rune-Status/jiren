package io.jiren.util

import java.nio.ByteBuffer

import akka.util.ByteString

/** @author Sino */
object ByteBufferExtensions {
  implicit final class ByteBufferExtensions(val buffer: ByteBuffer) extends AnyVal {
    def getBytes(amount: Int) = {
      val data = new Array[Byte](amount)
      buffer.get(data)
      ByteBuffer.wrap(data)
    }

    def toByteString = {
      buffer.flip()

      val byteString = ByteString(buffer)

      buffer.flip()
      buffer.clear()

      byteString.compact
    }

    def skip(amount: Int): Unit = {
      buffer.position(buffer.position() + amount)
    }
  }
}
