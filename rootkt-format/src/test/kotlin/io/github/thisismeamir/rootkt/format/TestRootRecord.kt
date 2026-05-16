package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.compression.algorithms.zlibCompress
import io.github.thisismeamir.rootkt.compression.models.CompressedBlock
import io.github.thisismeamir.rootkt.compression.models.CompressionType
import io.github.thisismeamir.rootkt.format.models.RootBlock
import io.github.thisismeamir.rootkt.format.walkers.walkKeys
import io.github.thisismeamir.rootkt.format.walkers.walkRecords
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

// TestRootRecord.kt
class TestRootRecord {

    private val payload = "some object data".repeat(20).toByteArray()

    private fun makeKeyBytes(objLen: Int, keyLen: Short, compressed: Boolean): ByteBuffer {
        val dataSize = if (compressed) objLen / 2 else objLen  // rough approximation
        val nbytes = keyLen + dataSize
        val buf = ByteBuffer.allocate(200).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(nbytes)
        buf.putShort(4)               // version
        buf.putInt(objLen)
        buf.putInt(0)                 // datime
        buf.putShort(keyLen)
        buf.putShort(1)               // cycle
        buf.putInt(100)               // seekKey
        buf.putInt(0)                 // seekPdir
        buf.put(5); buf.put("TH1F".toByteArray() + byteArrayOf(0))
        buf.put(4); buf.put("hist".toByteArray())
        buf.put(0)
        buf.rewind()
        return buf
    }

    @Test
    fun `RootBlock Raw returns data directly`() {
        val raw = RootBlock.Raw(payload)
        assertArrayEquals(payload, raw.data)
        assertEquals(payload.size, raw.rawSize)
    }

    @Test
    fun `RootBlock Compressed decompresses lazily`() {
        val compressed = zlibCompress(payload)
        val block = CompressedBlock(
            algorithm = CompressionType.ZLIB,
            level = 6,
            compressedSize = compressed.size,
            uncompressedSize = payload.size,
            data = compressed
        )
        val rootBlock = RootBlock.Compressed(block)
        assertArrayEquals(payload, rootBlock.data)
        assertEquals(payload.size, rootBlock.rawSize)
    }

    private val key2Start = 126
    private val buf get() = twoKeyBuffer()
    private val totalSize get(): Long {
        val b = twoKeyBuffer()
        b.position(key2Start)
        val nbytes2 = b.getInt(key2Start)
        return (key2Start + nbytes2).toLong()
    }

    @Test
    fun `walkRecords returns same count as walkKeys`() {
        val end = totalSize
        val b = twoKeyBuffer()
        val keys = b.walkKeys(0, end)
        b.rewind()
        val records = b.walkRecords(0, end)
        assertEquals(keys.size, records.size)
    }

    @Test
    fun `walkRecords preserves key metadata`() {
        val records = twoKeyBuffer().walkRecords(0, totalSize)
        assertEquals("TFile", records[0].key.className)
        assertEquals("TBasket", records[1].key.className)
        assertTrue(records[1].key.isLarge)
    }

    @Test
    fun `walkRecords key2 block is compressed and decompresses correctly`() {
        val records = twoKeyBuffer().walkRecords(0, totalSize)
        val block = records[1].block
        assertTrue(block is RootBlock.Compressed)
        val expected = "ReconstructedParticles data".repeat(20).toByteArray()
        assertArrayEquals(expected, block.data)
    }

    // TODO: open real file, walkRecords, assert first record className == "TFile"
    // TODO: open real file, find a compressed record, assert block.data.size == key.objLen
}