package com.thisismeamir.rootkt.format

import com.thisismeamir.rootkt.format.walkers.parseRootHeader
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TestRootFileHeader {

    // Bytes crafted from the real fcc_ha_ecm240_hbb.root xxd dump.
    // Header is exactly 100 bytes; we only need the first ~45 for the fixed fields.
    // Remaining bytes are zeroed (reserved padding region — valid per spec).
    private fun realFileHeaderBytes(): ByteBuffer {
        val bytes = ByteArray(100)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        buf.put("root".toByteArray())          // magic
        buf.putInt(0x0000f48e)                 // fVersion = 62606  (32-bit file)
        buf.putInt(0x00000064)                 // fBEGIN   = 100
        buf.putInt(0x10c2cf52.toInt())         // fEND     (32-bit)
        buf.putInt(0x01dc1e54)                 // fSeekFree
        buf.putInt(0x00000081)                 // fNbytesFree
        buf.putInt(0x00000008)                 // nfree
        buf.putInt(0x00000042)                 // fNbytesName = 66
        buf.put(0x04)                          // fUnits = 4
        buf.putInt(0x00000065)                 // fCompress
        buf.putInt(0x10c2a35e.toInt())         // fSeekInfo
        buf.putInt(0x00002bf4)                 // fNbytesInfo
        // UUID version (2 bytes) + UUID (16 bytes) — zeroed for this test
        buf.putShort(0x0001)
        repeat(16) { buf.put(0x00) }
        // remaining bytes to 100 are already zero (reserved)

        buf.rewind()
        return buf
    }

    @Test
    fun `parses magic bytes correctly`() {
        val header = realFileHeaderBytes().parseRootHeader()
        // implicit — parseRootHeader throws if magic != "root"
        assertNotNull(header)
    }

    @Test
    fun `parses version correctly`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertEquals(62606, header.version)
    }

    @Test
    fun `fBEGIN is 100`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertEquals(100, header.begin)
    }

    @Test
    fun `fUnits is 4 for 32-bit file`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertEquals(4, header.units)
    }

    @Test
    fun `isLargeFile is false for version below 1000000`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertFalse(header.isLargeFile)
    }

    @Test
    fun `fSeekInfo points to StreamerInfo record`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertEquals(0x10c2a35eL, header.seekInfo)
    }

    @Test
    fun `fNbytesName is 66`() {
        val header = realFileHeaderBytes().parseRootHeader()
        assertEquals(66, header.nbytesName)
    }

    @Test
    fun `throws on invalid magic bytes`() {
        val bytes = realFileHeaderBytes()
        // corrupt the magic
        bytes.put(0, 'X'.code.toByte())
        assertThrows<IllegalArgumentException> { bytes.parseRootHeader() }
    }

    @Test
    fun `large file flag triggers 8-byte pointer reads`() {
        val bytes = ByteArray(100)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        buf.put("root".toByteArray())
        buf.putInt(1_062_606)           // fVersion >= 1_000_000 → large file
        buf.putInt(100)                 // fBEGIN
        buf.putLong(0x0000000010c2cf52L) // fEND     (8 bytes)
        buf.putLong(0x0000000001dc1e54L) // fSeekFree (8 bytes)
        buf.putInt(0x00000081)
        buf.putInt(0x00000008)
        buf.putInt(0x00000042)
        buf.put(0x08)                   // fUnits = 8 for large files
        buf.putInt(0x00000065)
        buf.putLong(0x0000000010c2a35eL) // fSeekInfo (8 bytes)
        buf.putInt(0x00002bf4)
        buf.rewind()

        val header = buf.parseRootHeader()
        assertTrue(header.isLargeFile)
        assertEquals(8, header.units)
        assertEquals(0x10c2cf52L, header.end)
        assertEquals(0x10c2a35eL, header.seekInfo)
    }

    @Test
    fun `opens root file and parses header`() {
        val path = "src/test/resources/fcc_ha_haa.root"
        val bytes = File(path).readBytes()
        val header = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).parseRootHeader()

        assertEquals(100, header.begin)
        assertFalse(header.isLargeFile)
        assertTrue(header.seekInfo > 0)
        assertTrue(header.end > 100)
    }
}