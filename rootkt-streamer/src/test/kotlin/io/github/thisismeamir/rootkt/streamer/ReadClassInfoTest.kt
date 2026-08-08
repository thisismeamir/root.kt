package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.parsers.classinfo.K_CLASS_MASK
import io.github.thisismeamir.rootkt.format.parsers.classinfo.K_NEW_CLASS_TAG
import io.github.thisismeamir.rootkt.format.parsers.classinfo.parseClassInfo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReadClassInfoTest {

    @Test
    fun `parses new class tag with null-terminated name`() {
        val name = "TStreamerInfo"
        val bytes = ByteBuffer.allocate(4 + name.length + 1).order(ByteOrder.BIG_ENDIAN)
        bytes.putInt(K_NEW_CLASS_TAG)
        bytes.put(name.toByteArray(Charsets.UTF_8))
        bytes.put(0)
        bytes.flip()

        val info = bytes.parseClassInfo()
        assertTrue(info.isNewClass)
        assertEquals(name, info.className)
        assertNull(info.clIdx)
    }

    @Test
    fun `parses back-reference class tag and strips kClassMask`() {
        val offset = 192
        val tagged = offset or K_CLASS_MASK
        val buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(tagged)
        buf.flip()

        val info = buf.parseClassInfo()
        assertFalse(info.isNewClass)
        assertEquals(offset, info.clIdx)
        assertNull(info.className)
    }

    @Test
    fun `handles empty class name in new tag`() {
        val buf = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(K_NEW_CLASS_TAG)
        buf.put(0)
        buf.flip()

        val info = buf.parseClassInfo()
        assertEquals("", info.className)
    }
}