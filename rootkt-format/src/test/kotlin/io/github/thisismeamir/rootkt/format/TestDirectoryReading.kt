package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.walkers.buildDirectoryTree
import io.github.thisismeamir.rootkt.format.walkers.parseKey
import io.github.thisismeamir.rootkt.format.walkers.parseRecord
import io.github.thisismeamir.rootkt.format.walkers.parseRootHeader
import io.github.thisismeamir.rootkt.format.walkers.readTDirectory
import io.github.thisismeamir.rootkt.format.walkers.walkKeys
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDirectoryReading {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "simple_th1.root",
            "simple_th2.root",
            "simple_ttree.root",
            "array_branches.root",
            "subdirectory.root",
            "multi_tree.root",
            "uncompressed.root",
            "profile.root",
            "tgraph.root",
            "ntuple.root"
        ]
    )
    fun `parses file header`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        assertEquals(100, header.begin)
        assertTrue(header.end > header.begin)
    }


    @ParameterizedTest
    @ValueSource(
        strings = [
            "simple_th1.root",
            "simple_th2.root",
            "simple_ttree.root",
            "array_branches.root",
            "subdirectory.root",
            "multi_tree.root",
            "uncompressed.root",
            "profile.root",
            "tgraph.root",
            "ntuple.root"
        ]
    )
    fun `parses TKey at begin`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        buf.position(header.begin)
        val key = buf.parseKey()

        assertTrue(key.nbytes > 0)
        assertEquals(header.begin.toLong(), key.seekKey)
    }


    @ParameterizedTest
    @ValueSource(
        strings = [
            "simple_th1.root",
            "simple_th2.root",
            "simple_ttree.root",
            "array_branches.root",
            "subdirectory.root",
            "multi_tree.root",
            "uncompressed.root",
            "profile.root",
            "tgraph.root",
            "ntuple.root"
        ]
    )
    fun `parses TDirectory payload after key`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
//        println(buf.position())
        val header = buf.parseRootHeader()
        buf.position(header.begin)
//        println(buf.position())
        val key = buf.walkKeys(
            header.begin, header.end
        )

        buf.position(header.begin + header.nbytesName)
        val dir = buf.readTDirectory()


        assertEquals(0L, dir.seekParent)
        assertTrue(dir.nbytesKeys > 0)
    }

    @Test
    fun `finds subdirectory key and parses its TDirectory payload`() {
        val bytes = File("src/test/resources/subdirectory.root").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val keys = buf.walkKeys(header.begin, header.end)
        val subdirKey = keys.first { it.className == "TDirectory" }

        buf.position(subdirKey.seekKey.toInt() + subdirKey.keyLen)
        val subdir = buf.readTDirectory()

        assertEquals(header.begin.toLong(), subdir.seekParent)
    }

    @Test
    fun `verifies child key range for subdirectory`() {
        val bytes = File("src/test/resources/subdirectory.root").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()
        print("something")
        val keys = buf.walkKeys(header.begin, header.end)
        keys.forEach {
            print("===\n")
            println("${it.name}: seekPdir=${it.seekPdir}, seekKey=${it.seekKey}") }

    }
}
