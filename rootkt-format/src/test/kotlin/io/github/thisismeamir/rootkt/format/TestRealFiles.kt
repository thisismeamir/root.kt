package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.format.parsers.base.parseRootHeader
import io.github.thisismeamir.rootkt.format.walkers.walkKeys
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class TestRealFiles {
    fun getFilesInResources(): List<String> {
        val testResourcesDir = "src/test/resources"
        return File(testResourcesDir).walk()
            .filter { it.isFile }
            .map { it.absolutePath }
            .filter { it.endsWith(".root") }
            .toList()
    }

    fun getFileByName(name: String): File {
        val files = getFilesInResources()
        val matching = files.filter { it.endsWith("${name}.root") }
        if (matching.isEmpty()) {
            throw IllegalArgumentException("No .root file found with name: $name")
        }
        if (matching.size > 1) {
            throw IllegalArgumentException(
                "Multiple .root files found with name: $name. Matches: ${
                    matching.joinToString(
                        ", "
                    )
                }"
            )
        }
        return File(matching.first())
    }

    // Example test that just prints the paths of the .root files in resources.
    @Test
    fun `list root files in resources`() {
        val rootFiles = getFilesInResources()
        println("Found .root files in resources:")
        // Just printing the names:
        rootFiles.map { File(it).name }.forEach { println(it) }
    }


    // ========================
    // Simple TH1 ROOT file tests
    // ========================

    @Test
    fun `simple th1 root file parsing header`() {
        val buf = getFileByName("simple_th1")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from simple_th1.root")
    }

    @Test
    fun `simple th1 root file parsing keys`() {
        val buf = getFileByName("simple_th1")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        // We know that TFile is also considered a key, but they often have empty titles.
        // The actual data keys should have non-empty titles.
        // So we can check that at least 2 keys have non-empty titles, which would correspond to the histogram objects.
        assertEquals(2, keys.filter{it.title.isNotEmpty()}.size)
    }

    // ========================
    // Simple TH2 ROOT file tests
    // ========================

    @Test
    fun `simple th2 root file parsing header`() {
        val buf = getFileByName("simple_th2")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from simple_th2.root")
    }

    @Test
    fun `simple th2 root file parsing keys`() {
        val buf = getFileByName("simple_th2")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        // We know that TFile is also considered a key, but they often have empty titles.
        // The actual data keys should have non-empty titles.
        // So we can check that at least 2 keys have non-empty titles, which would correspond to the histogram objects.
        assertEquals(2, keys.filter{it.title.isNotEmpty()}.size)
    }

    // ========================
    // Simple TTree ROOT file tests
    // ========================

    @Test
    fun `simple ttree root file parsing header`() {
        val buf = getFileByName("simple_ttree")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from simple_ttree.root")
    }

    @Test
    fun `simple ttree root file parsing keys`() {
        val buf = getFileByName("simple_ttree")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("run", keys)
        assertNameExists("pt", keys)
        assertNameExists("eta", keys)
        assertNameExists("flag", keys)
        assertNameExists("events", keys)
        assertNameExists("StreamerInfo", keys)
    }


    // ========================
    // Array Branches ROOT file tests
    // ========================

    @Test
    fun `array branches root file parsing header`() {
        val buf = getFileByName("array_branches")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from array_branches.root")
    }

    @Test
    fun `array branches root file parsing keys`() {
        val buf = getFileByName("array_branches")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("n", keys)
        assertNameExists("vals", keys)
        assertNameExists("hits", keys)
    }

    // ========================
    // Subdirectories ROOT file tests
    // ========================
    @Test
    fun `subdirectory root file parsing header`() {
        val buf = getFileByName("subdirectory")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from subdirectory.root")
    }

    @Test
    fun `subdirectory root file parsing keys`() {
        val buf = getFileByName("subdirectory")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("detector", keys)
        assertNameExists("trigger", keys)
    }

    // ========================
    // Mutiltree ROOT file tests
    // ========================

    @Test
    fun `multi-tree root file parsing header`() {
        val buf = getFileByName("multi_tree")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from multi_tree.root")
    }

    @Test
    fun `multi-tree root file parsing keys`() {
        val buf = getFileByName("multi_tree")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("summary", keys)
        assertNameExists("electrons", keys)
        assertNameExists("muons", keys)
    }


    // ========================
    // Uncompressed ROOT file tests
    // ========================

    @Test
    fun `uncompressed root file parsing header`() {
        val buf = getFileByName("uncompressed")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from uncompressed.root")
    }

    @Test
    fun `uncompressed root file parsing keys`() {
        val buf = getFileByName("uncompressed")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("h", keys)
        assertFalse(keys.filter { it.name == "h" }[0].isCompressed, "Expected key 'h' to be uncompressed in uncompressed.root")
    }

    // ========================
    // Profile ROOT file tests
    // ========================

    @Test
    fun `profile root file parsing header`() {
        val buf = getFileByName("profile")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from profile.root")
    }

    @Test
    fun `profile root file parsing keys`() {
        val buf = getFileByName("profile")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("prof", keys)
    }

    // ========================
    // TGraf ROOT file tests
    // ========================

    @Test
    fun `tgraph root file parsing header`() {
        val buf = getFileByName("tgraph")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from tgraph.root")
    }

    @Test
    fun `tgraph root file parsing keys`() {
        val buf = getFileByName("tgraph")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("graph", keys)
    }


    // ========================
    // nTuple ROOT file tests
    // ========================

    @Test
    fun `ntuple root file parsing header`() {
        val buf = getFileByName("ntuple")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        assertNotNull(header, "Failed to parse ROOT file header from ntuple.root")
    }

    @Test
    fun `ntuple root file parsing keys`() {
        val buf = getFileByName("ntuple")
            .readBytes()
            .let {
                ByteBuffer.wrap(it).order(ByteOrder.BIG_ENDIAN)
            }

        val header = buf.parseRootHeader()
        val keys = buf.walkKeys(header.begin, header.end)
        assertNameExists("ntuple", keys)
    }
}