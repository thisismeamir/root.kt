package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import io.github.thisismeamir.rootkt.format.walkers.readRawTObjectArray
import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import io.github.thisismeamir.rootkt.streamer.models.TStreamerInfo
import java.nio.ByteBuffer

fun ByteBuffer.readTStreamerInfo(): TStreamerInfo {
    val payloadStart = position()
    val byteCount = readByteCount()
    val classInfo = readClassInfo()
    val remainingByteCount = readByteCount()
    val version = short
    val tName = readTNamed()
    val checkSum = int
    val classVersion = int

    val tStreamerElements = readRawTObjectArray()
        .readAsTStreamerElements()

    val end = payloadStart + 4 + byteCount
    position(end)
    return TStreamerInfo(
        classInfo = classInfo,
        version = version,
        tNamed = tName,
        fCheckSum = checkSum,
        fClassVersion = classVersion,
        objectArray = tStreamerElements
    )
}
