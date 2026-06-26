package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import io.github.thisismeamir.rootkt.format.walkers.readRawTObjectArray
import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import io.github.thisismeamir.rootkt.streamer.TStreamerInfo
import java.nio.ByteBuffer

fun ByteBuffer.readTStreamerInfo(): TStreamerInfo {
    val byteCount = readByteCount()
    val classInfo = readClassInfo()
    val afterClasInfoByteCount = readByteCount()
    val version = short
    val tNamed = readTNamed()
    val fCheckSum = int
    val fClassVersion = int
    val objectArray = readRawTObjectArray()
    val tStreamerELements = objectArray.readAsTStreamerElement()

    return TStreamerInfo(
        classInfo = classInfo,
        version = version,
        tNamed = tNamed,
        fCheckSum = fCheckSum,
        fClassVersion = fClassVersion,
        objectArray = tStreamerELements
    )
}