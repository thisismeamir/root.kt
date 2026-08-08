package io.github.thisismeamir.rootkt.format.parsers.data

import io.github.thisismeamir.rootkt.format.models.base.ClassInfo
import io.github.thisismeamir.rootkt.format.models.objectarray.RawTObjectArray
import io.github.thisismeamir.rootkt.format.parsers.base.parseTObject
import io.github.thisismeamir.rootkt.format.parsers.base.parseTString
import io.github.thisismeamir.rootkt.format.parsers.classinfo.parseAndResolveClassInfo
import io.github.thisismeamir.rootkt.format.service.ClassResolver
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.parseRawTObjectArray(classResolver: ClassResolver): RawTObjectArray {
    val byteCount = readByteCount()
    val className = parseAndResolveClassInfo(classResolver)

    val innerByteCount = readByteCount()
    val innerPayloadStart = position()

    val version = short
    val tObject = parseTObject()
    val name = parseTString()
    val numberOfObjects = int
    val fLowerBound = int

    val headerBytesReadInside = position() - innerPayloadStart
    val objectsPayloadSize = innerByteCount - headerBytesReadInside
    val objectsBaseOffset = position()
    val objectsData = ByteArray(objectsPayloadSize)
    get(objectsData)

    position(innerPayloadStart + innerByteCount)

    return RawTObjectArray(
        classInfo = ClassInfo(isNewClass = true, clIdx = null, className = className),
        version = version,
        tObject = tObject,
        fName = name,
        fLowerBound = fLowerBound,
        numberOfObjects = numberOfObjects,
        objects = objectsData,
        objectsBaseOffset = objectsBaseOffset
    )
}