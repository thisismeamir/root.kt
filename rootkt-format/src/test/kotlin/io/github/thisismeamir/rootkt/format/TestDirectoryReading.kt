package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.format.parsers.base.parseKey
import io.github.thisismeamir.rootkt.format.parsers.base.parseRootHeader
import io.github.thisismeamir.rootkt.format.parsers.data.parseTDirectoryData
import io.github.thisismeamir.rootkt.format.utils.printTree
import io.github.thisismeamir.rootkt.format.walkers.*
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
            "simple_th1.root", "simple_th2.root", "simple_ttree.root",
            "array_branches.root", "subdirectory.root", "multi_tree.root",
            "uncompressed.root", "profile.root", "tgraph.root", "ntuple.root", "deep_subdirectories.root"
        ]
    )
    fun `parses TDirectory payload after key`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        println(buf.position())
        val header = buf.parseRootHeader()
        buf.position(header.begin)
        println(buf.position())
        val key = buf.walkKeys(
            header.begin, header.end
        )

        buf.position(header.begin + header.nbytesName)
        val dir = buf.parseTDirectoryData()


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
        val subdir = buf.parseTDirectoryData()
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
            println("${it.name}: seekPdir=${it.seekPdir}, seekKey=${it.seekKey}")
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "simple_th1.root", "simple_th2.root", "simple_ttree.root",
            "array_branches.root", "subdirectory.root", "multi_tree.root",
            "uncompressed.root", "profile.root", "tgraph.root", "ntuple.root", "deep_subdirectories.root"
        ]
    )
    fun `Build directory tree for arbitrary files`(filename: String) {
        println("File: $filename")
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val root = buf.walkRootDirectory(header)
        root.printTree()

    }

    @ParameterizedTest
    @ValueSource(strings = ["subdirectory.root"])
    fun `Read own keys of a directory`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val topKeys = buf.walkKeys(header.begin, header.end)
        val detectorKey = topKeys.first { it.name == "detector" }
        val detectorDir = buf.walkDirectory(detectorKey.seekKey.toInt())
        println(detectorDir.data)
        val ownKeys = detectorDir.readOwnKeys(buf)
        println("detector's own keys: ${ownKeys.map { it.name to it.className }}")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "subdirectory.root"
        ]
    )
    fun `Walk subdirectories and objects of a directory`(filename: String) {
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val topKeys = buf.walkKeys(header.begin, header.end)
        val detectorKey = topKeys.first { it.name == "detector" }
        val detectorDir = buf.walkDirectory(detectorKey.seekKey.toInt())

        println("detector subdirectories: ${detectorDir.walkSubDirectories(buf).map { it.key.name }}")
        println("detector objects: ${detectorDir.objects(buf).map { it.name to it.className }}")

        val triggerKey = topKeys.first { it.name == "trigger" }
        val triggerDir = buf.walkDirectory(triggerKey.seekKey.toInt())

        println("trigger subdirectories: ${triggerDir.walkSubDirectories(buf).map { it.key.name }}")
        println("trigger objects: ${triggerDir.objects(buf).map { it.name to it.className }}")
    }
}