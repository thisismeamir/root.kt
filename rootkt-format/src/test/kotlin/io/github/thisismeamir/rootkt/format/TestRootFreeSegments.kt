package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.format.walkers.parseFreeSegments
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test

// TestRootFreeSegments.kt
class TestRootFreeSegments {

    private fun freeSegmentsBuffer(large: Boolean): ByteBuffer {
        val buf = ByteBuffer.allocate(256).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(3)  // nfree
        if (large) {
            buf.putLong(1000L); buf.putLong(1999L)  // gap 1
            buf.putLong(5000L); buf.putLong(7999L)  // gap 2
            buf.putLong(9029L); buf.putLong(Long.MAX_VALUE)  // virtual end entry
        } else {
            buf.putInt(1000); buf.putInt(1999)
            buf.putInt(5000); buf.putInt(7999)
            buf.putInt(9029); buf.putInt(Int.MAX_VALUE)
        }
        buf.rewind()
        return buf
    }

    @Test
    fun `parses correct number of segments`() {
        val fs = freeSegmentsBuffer(false).parseFreeSegments(large = false)
        assertEquals(3, fs.segments.size)
    }

    @Test
    fun `onDisk excludes last virtual entry`() {
        val fs = freeSegmentsBuffer(false).parseFreeSegments(large = false)
        assertEquals(2, fs.onDisk.size)
    }

    @Test
    fun `segment sizes are correct`() {
        val fs = freeSegmentsBuffer(false).parseFreeSegments(large = false)
        assertEquals(1000L, fs.onDisk[0].size)  // 1999 - 1000 + 1
        assertEquals(3000L, fs.onDisk[1].size)  // 7999 - 5000 + 1
    }

    @Test
    fun `totalFreeBytes sums onDisk segments only`() {
        val fs = freeSegmentsBuffer(false).parseFreeSegments(large = false)
        assertEquals(4000L, fs.totalFreeBytes)
    }

    @Test
    fun `large file uses 8-byte offsets`() {
        val fs = freeSegmentsBuffer(true).parseFreeSegments(large = true)
        assertEquals(1000L, fs.onDisk[0].first)
        assertEquals(1999L, fs.onDisk[0].last)
    }

    // TODO: open real file, parse FreeSegments at fSeekFree, assert segments not empty
}