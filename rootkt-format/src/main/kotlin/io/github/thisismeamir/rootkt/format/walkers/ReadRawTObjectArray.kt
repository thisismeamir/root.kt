package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RawTObjectArray
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.readRawTObjectArray(): RawTObjectArray {
    val byteCount = readByteCount()
    val classInfo = readClassInfo()

    // The inner remaining bytes counter starts exactly here
    val innerByteCount = readByteCount()
    val innerPayloadStart = position()

    val version = short
    val tObject = readTObject()
    val name = readTString() // Handles the unnamed 0-byte layout cleanly
    val numberOfObjects = int
    val fLowerBound = int

    // Compute remaining bytes assigned strictly to the objects array payload frame
    val headerBytesReadInside = position() - innerPayloadStart
    val objectsPayloadSize = innerByteCount - headerBytesReadInside

    val objectsData = ByteArray(objectsPayloadSize)
    get(objectsData)

    // Ensure the stream cursor lands exactly at the end boundary of the TObjArray frame
    position(innerPayloadStart + innerByteCount)

    return RawTObjectArray(
        classInfo = classInfo,
        version = version,
        tObject = tObject,
        fName = name,
        fLowerBound = fLowerBound,
        numberOfObjects = numberOfObjects,
        objects = objectsData
    )
}