package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import io.github.thisismeamir.rootkt.format.walkers.readRawTObjectArray
import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import java.nio.ByteBuffer

fun ByteBuffer.readRawStreamerInfoBlock(): ByteArray {
    val payloadStart = position()
    val byteCount = readByteCount()
    val classInfo = readClassInfo()
    val remainingByteCount = readByteCount()
    val version = short
    val tName = readTNamed()
    val checkSum = int
    val classVersion = int

    val objectArray = readRawTObjectArray()


//    val blockSize = end - payloadStart
    val block = ByteArray(byteCount)
    get(block)
    val end = payloadStart + 4 + byteCount
    position(end)
    return block
}