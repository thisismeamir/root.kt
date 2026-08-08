package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.format.models.list.RawTList
import io.github.thisismeamir.rootkt.format.models.list.TList
import io.github.thisismeamir.rootkt.streamer.models.streamerinfo.TStreamerInfo
import io.github.thisismeamir.rootkt.format.service.ClassResolver
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun RawTList.readAsTStreamerInfos(classResolver: ClassResolver): TList<TStreamerInfo> {
    if (numberOfObjects <= 0) return TList(
        byteCount = this.byteCount,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        objects = emptyList()
    )

    val localResolver = classResolver.setResolverZero(objectsBaseOffset)
    val buffer = ByteBuffer.wrap(this.objects).order(ByteOrder.BIG_ENDIAN)
    val elementsList = ArrayList<TStreamerInfo>(numberOfObjects)
    for (i in 0 until (numberOfObjects - 1)) {
        val element = buffer.readTStreamerInfo(localResolver)
        elementsList.add(element)
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