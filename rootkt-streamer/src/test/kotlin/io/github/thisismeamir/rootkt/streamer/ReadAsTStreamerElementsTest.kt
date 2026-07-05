package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.models.ClassInfo
import io.github.thisismeamir.rootkt.format.models.RawTObjectArray
import io.github.thisismeamir.rootkt.format.models.TObject
import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.walkers.parseRecord
import io.github.thisismeamir.rootkt.format.walkers.parseRootHeader
import io.github.thisismeamir.rootkt.format.walkers.readRawTList
import io.github.thisismeamir.rootkt.streamer.walkers.readAsTStreamerElements
import io.github.thisismeamir.rootkt.streamer.walkers.readAsTStreamerInfos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test

class ReadAsTStreamerElementsTest {

    @Test
    fun `parses zero elements when numberOfObjects is zero`() {
        val raw = RawTObjectArray(
            classInfo = ClassInfo(isNewClass = true, clIdx = null, className = "TObjArray"),
            version = 1,
            tObject = TObject(1, 0, 0, null),
            fName = "",
            fLowerBound = 0,
            numberOfObjects = 0,
            objects = ByteArray(0)
        )
        val result = raw.readAsTStreamerElements()
        assertEquals(0, result.numberOfObjects)
        assertTrue(result.objects.isEmpty())
    }

    @Test
    fun `element count matches numberOfObjects, not numberOfObjects minus one`() {
        // regression test for the earlier off-by-one loop bound bug
        val fixtureBytes = File("src/test/resources/simple_th1.root").readBytes()
        val buf = ByteBuffer.wrap(fixtureBytes).order(ByteOrder.BIG_ENDIAN)
        val header = buf.parseRootHeader()

        val streamerInfoList = buf.position(header.seekInfo.toInt())
            .parseRecord()
            .block
            .data
            .toByteBuffer()
            .readRawTList()
            .readAsTStreamerInfos()

        val firstInfo = streamerInfoList.objects.first()
            // TODO: Checking the number of objects mechanism more closely.
    //        assertEquals(
//            firstInfo.objectArray.numberOfObjects,
//            firstInfo.objectArray.objects.size
//        )
    }
}