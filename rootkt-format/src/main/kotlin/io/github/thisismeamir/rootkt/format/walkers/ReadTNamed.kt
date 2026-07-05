package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.TNamed
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.readTNamed(): TNamed {
    // 1. Read the byte count and track the payload start position
    val payloadStartPosition = position()
    val byteCount = readByteCount()
    val version = short

    // 2. Delegate to your existing sub-parsers
    val tObject = readTObject()
    val name = readTString()
    val title = readTString()
    val newpos = payloadStartPosition + byteCount +4
    position(newpos)

    return TNamed(
        byteCount = byteCount,
        version = version,
        obj = tObject,
        name = name,
        title = title
    )
}