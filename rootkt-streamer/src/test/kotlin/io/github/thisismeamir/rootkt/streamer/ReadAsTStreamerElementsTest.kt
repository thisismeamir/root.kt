package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.parsers.data.parseRecord
import io.github.thisismeamir.rootkt.format.parsers.base.parseRootHeader
import io.github.thisismeamir.rootkt.streamer.readers.readAsStreamerInfoRecord
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test

class ReadAsTStreamerElementsTest {



    @Test
    fun `element count matches numberOfObjects, not numberOfObjects minus one`() {
        // regression test for the earlier off-by-one loop bound bug
        val fixtureBytes = File("src/test/resources/simple_th1.root").readBytes()
        val buf = ByteBuffer.wrap(fixtureBytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val streamerInfoList = buf.position(header.seekInfo.toInt())
            .parseRecord()
            .readAsStreamerInfoRecord()


    }
}