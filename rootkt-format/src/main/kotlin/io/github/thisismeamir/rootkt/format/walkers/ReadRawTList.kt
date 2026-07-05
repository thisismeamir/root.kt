package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RawTList
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.readRawTList(): RawTList {
    val byteCount = readByteCount()
    val payloadStart = position() // start of versioned payload
    val version = short
    val tObject = readTObject()
    val fName = readTString()
    val numberOfObjects = int
    val objectsPayloadSize = byteCount - position()
    val objectsData = ByteArray(objectsPayloadSize)
    get(objectsData)

    val end = payloadStart + byteCount
    position(end)

    return RawTList(
        byteCount = byteCount,
        version = version,
        tObject = tObject ,
        fName = fName,
        numberOfObjects = numberOfObjects,
        objects = objectsData
    )
}