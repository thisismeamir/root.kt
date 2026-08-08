package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.format.models.objectarray.RawTObjectArray
import io.github.thisismeamir.rootkt.format.models.objectarray.TObjectArray
import io.github.thisismeamir.rootkt.format.service.ClassResolver
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerElement
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun RawTObjectArray.readAsTStreamerElements(classResolver: ClassResolver): TObjectArray<TStreamerElement> {
    if (numberOfObjects <= 0) return TObjectArray(
        classInfo = this.classInfo,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        fLowerBound = this.fLowerBound,
        objects = emptyList()
    )

    val localResolver = classResolver.setResolverZero(objectsBaseOffset)
    val buffer = ByteBuffer.wrap(this.objects).order(ByteOrder.BIG_ENDIAN)
    val elementsList = ArrayList<TStreamerElement>(numberOfObjects)

    for (i in 0 until (numberOfObjects - 1)) {
        val element = buffer.readTStreamerElement(localResolver)
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