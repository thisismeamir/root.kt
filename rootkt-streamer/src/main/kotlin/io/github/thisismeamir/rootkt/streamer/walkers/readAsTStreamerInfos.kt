package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.models.RawTList
import io.github.thisismeamir.rootkt.format.models.TList
import io.github.thisismeamir.rootkt.streamer.models.TStreamerInfo
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun RawTList.readAsTStreamerInfos(): TList<TStreamerInfo> {
    if (numberOfObjects <= 0) return TList(
        byteCount = this.byteCount,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        objects = emptyList()
    )

    val buffer = ByteBuffer.wrap(this.objects).order(ByteOrder.BIG_ENDIAN)
    val elementsList = ArrayList<TStreamerInfo>(numberOfObjects)
    for (i in 0 until (numberOfObjects - 1 )) {

        val pos = buffer.position()
        val element = buffer.readTStreamerInfo()
        elementsList.add(element)
        // One byte for option which should be disregarded for now
        // TODO: Check this explicitly within the documentation.
        val optionLen = buffer.get().toInt() and 0xFF
        if (optionLen > 0) {
            buffer.position(buffer.position() + optionLen)
        }
    }
    return TList(
        byteCount = this.byteCount,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        objects = elementsList
    )
}