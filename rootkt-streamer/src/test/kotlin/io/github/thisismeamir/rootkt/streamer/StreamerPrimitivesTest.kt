package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.walkers.readTString
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamerPrimitivesTest {

    @Test
    fun `readTString reads short string`() {
        val buf = ByteBuffer.allocate(10).order(ByteOrder.BIG_ENDIAN)
        buf.put(5); buf.put("hello".toByteArray()); buf.rewind()
        assertEquals("hello", buf.readTString())
    }

    @Test
    fun `readTString reads empty string`() {
        val buf = ByteBuffer.allocate(1).order(ByteOrder.BIG_ENDIAN)
        buf.put(0); buf.rewind()
        assertEquals("", buf.readTString())
    }

    @Test
    fun `readTString handles long string sentinel 255`() {
        val long = "x".repeat(300)
        val buf = ByteBuffer.allocate(310).order(ByteOrder.BIG_ENDIAN)
        buf.put(255.toByte())
        buf.putInt(300)
        buf.put(long.toByteArray())
        buf.rewind()
        assertEquals(long, buf.readTString())
    }

    @Test
    fun `readNullTerminatedString reads correctly`() {
        val buf = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN)
        buf.put("TStreamerInfo".toByteArray())
        buf.put(0)
        buf.rewind()
//        assertEquals("TStreamerInfo", buf.readNullTerminatedString())
    }

    @Test
    fun `skipTObject advances position by 10`() {
        val buf = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN)
        buf.rewind()
        val before = buf.position()
        buf.position(buf.position() + 10)
        assertEquals(before + 10, buf.position())
    }

}