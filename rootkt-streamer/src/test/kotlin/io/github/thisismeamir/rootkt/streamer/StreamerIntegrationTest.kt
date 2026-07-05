package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.walkers.parseRecord
import io.github.thisismeamir.rootkt.format.walkers.parseRootHeader
import io.github.thisismeamir.rootkt.format.walkers.readRawTList
import io.github.thisismeamir.rootkt.streamer.walkers.readAsTStreamerInfos
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder


class StreamerIntegrationTest {

//    private fun registry(filename: String): StreamerRegistry {
//        val bytes = File("src/test/resources/$filename").readBytes()
//        return StreamerRegistry(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN))
//    }

    // --- basic loading ---

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
    fun `loads streamer list from all fixtures without error`(filename: String) {
        println(filename)
        println("=======")
        val bytes = File("src/test/resources/$filename").readBytes()
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

    val streamerInfoRecords = buf.position(header.seekInfo.toInt())
        .parseRecord()
        .block
        .data
        .toByteBuffer()
        .readRawTList()
        .readAsTStreamerInfos()


    }

}

