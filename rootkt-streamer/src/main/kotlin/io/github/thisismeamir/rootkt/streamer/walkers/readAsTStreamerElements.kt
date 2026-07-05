package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.models.RawTObjectArray
import io.github.thisismeamir.rootkt.format.models.TObjectArray
import io.github.thisismeamir.rootkt.streamer.models.TStreamerElement
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun RawTObjectArray.readAsTStreamerElements(): TObjectArray<TStreamerElement> {
    if (numberOfObjects <= 0) return TObjectArray(
        classInfo = this.classInfo,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        fLowerBound = this.fLowerBound,
        objects = emptyList()
    )

    // Wrap the raw payload segment into a dedicated readable cursor view
    val buffer = ByteBuffer.wrap(this.objects).order(ByteOrder.BIG_ENDIAN)
    val elementsList = ArrayList<TStreamerElement>(numberOfObjects)

    for (i in 0 until (numberOfObjects - 1)) {

        val element = buffer.readTStreamerElement()
        elementsList.add(element)

    }

    return TObjectArray(
        classInfo = this.classInfo,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        fLowerBound = this.fLowerBound,
        objects = elementsList
    )
}