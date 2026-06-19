package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.compression.algorithms.zlibCompress
import io.github.thisismeamir.rootkt.compression.models.CompressionType
import io.github.thisismeamir.rootkt.format.models.Block
import io.github.thisismeamir.rootkt.format.walkers.parseCompressedBlock
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.assertThrows

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class TestParseCompressedBlock {

    private fun zlibBlockBytes(payload: ByteArray): ByteBuffer {
        val compressed = zlibCompress(payload)
        val buf = ByteBuffer.allocate(9 + compressed.size).order(ByteOrder.BIG_ENDIAN)
        buf.put("ZL".toByteArray())
        buf.put(6)  // level
        // 3-byte little-endian compressed size
        buf.put((compressed.size and 0xFF).toByte())
        buf.put((compressed.size shr 8 and 0xFF).toByte())
        buf.put((compressed.size shr 16 and 0xFF).toByte())
        // 3-byte little-endian uncompressed size
        buf.put((payload.size and 0xFF).toByte())
        buf.put((payload.size shr 8 and 0xFF).toByte())
        buf.put((payload.size shr 16 and 0xFF).toByte())
        buf.put(compressed)
        buf.rewind()
        return buf
    }

    private val payload = "FCC particle data".repeat(50).toByteArray()

    @Test
    fun `parses algorithm correctly`() {
        val block = zlibBlockBytes(payload).parseCompressedBlock()
        assertEquals(CompressionType.ZLIB, block.algorithm)
    }

    @Test
    fun `parses compressed and uncompressed sizes`() {
        val block = zlibBlockBytes(payload).parseCompressedBlock()
        assertEquals(payload.size, block.uncompressedSize)
        assertEquals(block.compressedSize, block.data.size)
    }

    @Test
    fun `decompresses correctly via RootBlock`() {
        val block = zlibBlockBytes(payload).parseCompressedBlock()
        val rootBlock = Block.Compressed(block)
        assertArrayEquals(payload, rootBlock.data)
    }

    @Test
    fun `throws on unknown magic`() {
        val buf = ByteBuffer.allocate(9).order(ByteOrder.BIG_ENDIAN)
        buf.put("XX".toByteArray())
        repeat(7) { buf.put(0) }
        buf.rewind()
        assertThrows<IllegalStateException> { buf.parseCompressedBlock() }
    }
}