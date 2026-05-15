package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.format.walkers.parseKey
import io.github.thisismeamir.rootkt.format.walkers.walkKeys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TestRootTKey {

    // Bytes from fcc_ha_ecm240_hbb.root at offset 100 (fBEGIN), xxd-verified.
    private fun realKeyBytes(): ByteBuffer {
        val bytes = ByteArray(64)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        buf.putInt(0x0000007e)           // fNbytes   = 126
        buf.putShort(0x0004)             // fVersion  = 4
        buf.putInt(0x0000004d)           // fObjLen   = 77
        buf.putInt(0x7c6ea255.toInt())   // fDatime
        buf.putShort(0x0031)             // fKeyLen   = 49
        buf.putShort(0x0001)             // fCycle    = 1
        buf.putInt(0x00000064)           // fSeekKey  = 100
        buf.putInt(0x00000000)           // fSeekPdir = 0  (root dir, no parent)

        // className: len=5, "TFile"
        buf.put(0x05)
        buf.put("TFile".toByteArray())

        // name: len=15, "events_100.root"
        buf.put(0x0f)
        buf.put("events_100.root".toByteArray())

        // title: len=0
        buf.put(0x00)

        buf.rewind()
        return buf
    }

    @Test
    fun `parses fNbytes correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals(126, key.nbytes)
    }

    @Test
    fun `parses fVersion correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals(4, key.version)
    }

    @Test
    fun `parses fObjLen correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals(77, key.objLen)
    }

    @Test
    fun `parses fKeyLen correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals(49, key.keyLen)
    }

    @Test
    fun `parses fCycle correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals(1, key.cycle)
    }

    @Test
    fun `fSeekKey points to itself at fBEGIN`() {
        val key = realKeyBytes().parseKey()
        assertEquals(100L, key.seekKey)
    }

    @Test
    fun `fSeekPdir is zero for root directory key`() {
        val key = realKeyBytes().parseKey()
        assertEquals(0L, key.seekPdir)
    }

    @Test
    fun `parses className correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals("TFile", key.className)
    }

    @Test
    fun `parses name correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals("events_100.root", key.name)
    }

    @Test
    fun `parses empty title correctly`() {
        val key = realKeyBytes().parseKey()
        assertEquals("", key.title)
    }

    @Test
    fun `isLarge is false for version 4`() {
        val key = realKeyBytes().parseKey()
        assertFalse(key.isLarge)
    }

    @Test
    fun `isCompressed is true when objLen differs from dataSize`() {
        val key = realKeyBytes().parseKey()
        // dataSize = nbytes - keyLen = 126 - 49 = 77, objLen = 77 → not compressed here
        assertFalse(key.isCompressed)
    }

    // TODO: open real file, seek to fBEGIN=100, parse first TKey and assert className == "TFile"
     @Test
     fun `reads first TKey from real file`() {
         val path = "../test-files/fcc_ha_ecm240_hbb.root"
         val channel = FileInputStream(path).channel
         val buf = channel.map(FileChannel.MapMode.READ_ONLY, 100, 64)
         buf.order(ByteOrder.BIG_ENDIAN)
         val key = buf.parseKey()
        assertEquals("TFile", key.className)
         assertEquals(100L, key.seekKey)
     }

    // TODO: open real file, walk all keys from fBEGIN and assert count > 0
    // @Test
    // fun `walks all TKeys from fBEGIN`() {
    //     val path = "/path/to/your/file.root"
    //     ...
    // }

    // --- walker tests ---

    private fun twoKeyBuffer(): ByteBuffer {
        val buf = ByteBuffer.allocate(600).order(ByteOrder.BIG_ENDIAN)

        // Key 1 at offset 0 (represents fBEGIN=100 in file, here at 0 for simplicity)
        buf.putInt(126)              // fNbytes
        buf.putShort(4)              // fVersion (small key)
        buf.putInt(77)               // fObjLen
        buf.putInt(0x7c6ea255.toInt()) // fDatime
        buf.putShort(49)             // fKeyLen
        buf.putShort(1)              // fCycle
        buf.putInt(100)              // fSeekKey
        buf.putInt(0)                // fSeekPdir
        buf.put(5); buf.put("TFile".toByteArray())
        buf.put(15); buf.put("events_100.root".toByteArray())
        buf.put(0)                   // empty title
        // pad to 126 bytes total
        val key1End = 126
        buf.position(key1End)

        // Key 2 at offset 126 (large key, fVersion=1004)
        buf.putInt(316)              // fNbytes
        buf.putShort(1004)           // fVersion (large key)
        buf.putInt(28656)            // fObjLen
        buf.putInt(0x7c6ea260.toInt()) // fDatime
        buf.putShort(105)            // fKeyLen
        buf.putShort(0)              // fCycle
        buf.putLong(226)             // fSeekKey (8 bytes)
        buf.putLong(100)             // fSeekPdir (8 bytes)
        buf.put(7); buf.put("TBasket".toByteArray())
        buf.put(36); buf.put("ReconstructedParticles.covMatrix[10]".toByteArray())
        buf.put(6); buf.put("events".toByteArray())
        // pad to 126+316 bytes total
        buf.position(key1End + 316)

        buf.rewind()
        return buf
    }

    @Test
    fun `walker returns two keys`() {
        val keys = twoKeyBuffer().walkKeys(begin = 0, end = (126 + 316).toLong())
        assertEquals(2, keys.size)
    }

    @Test
    fun `walker first key is TFile`() {
        val keys = twoKeyBuffer().walkKeys(begin = 0, end = (126 + 316).toLong())
        assertEquals("TFile", keys[0].className)
    }

    @Test
    fun `walker second key is TBasket and is large`() {
        val keys = twoKeyBuffer().walkKeys(begin = 0, end = (126 + 316).toLong())
        assertEquals("TBasket", keys[1].className)
        assertTrue(keys[1].isLarge)
    }

    @Test
    fun `walker second key has correct name and title`() {
        val keys = twoKeyBuffer().walkKeys(begin = 0, end = (126 + 316).toLong())
        assertEquals("ReconstructedParticles.covMatrix[10]", keys[1].name)
        assertEquals("events", keys[1].title)
    }

    @Test
    fun `walker stops on negative nbytes (free segment)`() {
        val buf = ByteBuffer.allocate(200).order(ByteOrder.BIG_ENDIAN)
        // valid key
        buf.putInt(126); buf.putShort(4); buf.putInt(77)
        buf.putInt(0); buf.putShort(49); buf.putShort(1)
        buf.putInt(100); buf.putInt(0)
        buf.put(5); buf.put("TFile".toByteArray())
        buf.put(15); buf.put("events_100.root".toByteArray())
        buf.put(0)
        buf.position(126)
        // free segment marker
        buf.putInt(-50)
        buf.rewind()

        val keys = buf.walkKeys(begin = 0, end = 200L)
        assertEquals(1, keys.size)
    }

    // TODO: open real file, walk all keys and assert size > 0
    // @Test
    // fun `walks all keys in real file`() {
    //     val path = "/path/to/your/file.root"
    //     val bytes = File(path).readBytes()
    //     val header = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).parseRootHeader()
    //     val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
    //     val keys = walkKeys(buf, header.begin, header.end)
    //     assertTrue(keys.isNotEmpty())
    //     assertEquals("TFile", keys.first().className)
    // }
}