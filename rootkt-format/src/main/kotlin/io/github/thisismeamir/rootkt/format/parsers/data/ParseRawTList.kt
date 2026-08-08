package io.github.thisismeamir.rootkt.format.parsers.data

import io.github.thisismeamir.rootkt.format.models.list.RawTList
import io.github.thisismeamir.rootkt.format.parsers.base.parseTObject
import io.github.thisismeamir.rootkt.format.parsers.base.parseTString
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.parseRawTList(): RawTList {
    val byteCount = readByteCount()
    val payloadStart = position()
    val version = short
    val tObject = parseTObject()
    val fName = parseTString()
    val numberOfObjects = int
    val objectsPayloadSize = byteCount - position()
    val objectsBaseOffset = position()
    val objectsData = ByteArray(objectsPayloadSize)
    get(objectsData)
    val end = payloadStart + byteCount
    position(end)
    return RawTList(
        byteCount = byteCount,
        version = version,
        tObject = tObject,
        fName = fName,
        numberOfObjects = numberOfObjects,
        objects = objectsData,
        objectsBaseOffset = objectsBaseOffset
    )
}