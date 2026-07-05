package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import java.nio.ByteBuffer

fun ByteBuffer.readRawTStreamerElementBlock(): ByteArray {
    val payloadStart = position()
    val byteCount = readByteCount()
    val classInfo = readClassInfo()
    val remainingBytes = readByteCount()
    println("""
        ===
        starting from : $payloadStart,
        bytecount : $byteCount,
        class name : ${classInfo.className},
        clIdx : ${classInfo.clIdx},
        after class bytecount : $remainingBytes
    """.trimIndent())
    val block = ByteArray(0)

    val end = payloadStart + 4 + byteCount
    position(end)
    return block
}