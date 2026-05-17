package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.streamer.models.DataType
import io.github.thisismeamir.rootkt.streamer.walkers.readTString
import io.github.thisismeamir.rootkt.streamer.walkers.readNullTerminatedString
import io.github.thisismeamir.rootkt.streamer.walkers.skipTObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class TestStreamerPrimitives {

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
        assertEquals("TStreamerInfo", buf.readNullTerminatedString())
    }

    @Test
    fun `skipTObject advances position by 10`() {
        val buf = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN)
        buf.rewind()
        val before = buf.position()
        buf.skipTObject()
        assertEquals(before + 10, buf.position())
    }

    @Test
    fun `EDataType resolves known codes`() {
        assertEquals(DataType.INT, DataType.fromCode(3))
        assertEquals(DataType.FLOAT, DataType.fromCode(5))
        assertEquals(DataType.DOUBLE, DataType.fromCode(8))
        assertEquals(DataType.BOOL, DataType.fromCode(18))
        assertEquals(DataType.STL, DataType.fromCode(300))
        assertEquals(DataType.UNKNOWN, DataType.fromCode(999))
    }

    @Test
    fun `EDataType array offset is base + 20`() {
        // fixed-size array of int = 3 + 20 = 23
        assertEquals(23, DataType.INT.code + 20)
    }

    @Test
    fun `EDataType pointer offset is base + 40`() {
        // pointer to float = 5 + 40 = 45
        assertEquals(45, DataType.FLOAT.code + 40)
    }
}