package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadTNamedTest {

    private fun buildTNamedBytes(
        version: Short,
        name: String,
        title: String
    ): ByteArray {
        // TObject: version(2) + uniqueID(4) + bits(4), unreferenced
        val tObjectBytes = ByteBuffer.allocate(10).order(ByteOrder.BIG_ENDIAN).apply {
            putShort(1)
            putInt(0)
            putInt(0) // fBits, kIsReferencedMask not set
        }.array()

        val nameBytes = byteArrayOf(name.length.toByte()) + name.toByteArray(Charsets.UTF_8)
        val titleBytes = byteArrayOf(title.length.toByte()) + title.toByteArray(Charsets.UTF_8)

        val versionBytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).apply {
            putShort(version)
        }.array()

        val payload = versionBytes + tObjectBytes + nameBytes + titleBytes
        val byteCount = payload.size

        val full = ByteBuffer.allocate(4 + payload.size).order(ByteOrder.BIG_ENDIAN)
        full.putInt(byteCount)
        full.put(payload)
        return full.array()
    }

    @Test
    fun `parses name and title correctly`() {
        val bytes = buildTNamedBytes(4, "fSomeMember", "a data member")
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        val named = buf.readTNamed()
        assertEquals("fSomeMember", named.name)
        assertEquals("a data member", named.title)
        assertEquals(4, named.version)
    }

    @Test
    fun `handles empty title`() {
        val bytes = buildTNamedBytes(4, "fX", "")
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        val named = buf.readTNamed()
        assertEquals("fX", named.name)
        assertEquals("", named.title)
    }

    @Test
    fun `leaves cursor exactly at end of TNamed block`() {
        val bytes = buildTNamedBytes(4, "fY", "title") + byteArrayOf(0x7A)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

        buf.readTNamed()
        assertEquals(0x7A, buf.get().toInt() and 0xFF)
    }
}