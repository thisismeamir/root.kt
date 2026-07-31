package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test

class ReadByteCountTest {

    @Test
    fun `reads unmasked byte count as-is`() {
        val buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(328)
        buf.flip()
        assertEquals(328, buf.readByteCount())
    }

    @Test
    fun `strips kByteCountMask flag bit`() {
        val buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(0x40000148) // flagged, true value 0x148 = 328
        buf.flip()
        assertEquals(328, buf.readByteCount())
    }

    @Test
    fun `handles zero byte count`() {
        val buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(0)
        buf.flip()
        assertEquals(0, buf.readByteCount())
    }

    @Test
    fun `advances position by exactly four bytes`() {
        val buf = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(100)
        buf.putInt(200)
        buf.flip()
        buf.readByteCount()
        assertEquals(4, buf.position())
    }
}