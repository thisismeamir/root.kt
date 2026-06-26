package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RawTList
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.readRawTList(): RawTList {
    val byteCount = readByteCount()
    val payloadStartPosition = position()

    val version = short

    // 1. Consume the TObject base class bytes
    val tObject = readTObject()
    val fName  = readTString()

    val numberOfObjects = int

    // 3. Compute remaining bytes based on actual position reached
    val headerBytesRead = position() - payloadStartPosition
    val objectsPayloadSize = byteCount - headerBytesRead

    val objectsData = ByteArray(objectsPayloadSize)
    get(objectsData)

    // 4. Align perfectly to the end of the TList record boundary
    position(payloadStartPosition + byteCount)

    return RawTList(
        byteCount = byteCount,
        version = version,
        tObject = tObject ,
        fName = fName,
        numberOfObjects = numberOfObjects,
        objects = objectsData
    )
}