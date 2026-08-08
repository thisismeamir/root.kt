package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.format.models.base.ClassInfo
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.parsers.data.parseRawTObjectArray
import io.github.thisismeamir.rootkt.format.parsers.base.parseTNamed
import io.github.thisismeamir.rootkt.streamer.models.streamerinfo.TStreamerInfo
import io.github.thisismeamir.rootkt.format.service.ClassResolver
import io.github.thisismeamir.rootkt.format.parsers.classinfo.parseAndResolveClassInfo
import java.nio.ByteBuffer

fun ByteBuffer.readTStreamerInfo(classResolver: ClassResolver): TStreamerInfo {
    val payloadStart = position()
    val byteCount = readByteCount()
    val className = parseAndResolveClassInfo(classResolver)
    val remainingByteCount = readByteCount()
    val version = short
    val tName = parseTNamed()
    val checkSum = int
    val classVersion = int

    val tStreamerElements = parseRawTObjectArray(classResolver)
        .readAsTStreamerElements(classResolver)

    val end = payloadStart + 4 + byteCount
    position(end)
    return TStreamerInfo(
        classInfo = ClassInfo(isNewClass = true, clIdx = null, className = className),
        version = version,
        tNamed = tName,
        fCheckSum = checkSum,
        fClassVersion = classVersion,
        objectArray = tStreamerElements
    )
}
