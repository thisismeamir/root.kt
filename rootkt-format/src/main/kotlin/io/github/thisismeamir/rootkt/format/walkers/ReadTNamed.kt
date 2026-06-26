package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.TNamed
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import java.nio.ByteBuffer

fun ByteBuffer.readTNamed(): TNamed {
    // 1. Read the byte count and track the payload start position
    val byteCount = readByteCount()
    val payloadStartPosition = position()

    val version = short

    // 2. Delegate to your existing sub-parsers
    val tObject = readTObject()
    val name = readTString()
    val title = readTString()

    // 3. Guarantee alignment by anchoring to the end of the TNamed frame boundary
    position(payloadStartPosition + byteCount)

    return TNamed(
        byteCount = byteCount,
        version = version,
        obj = tObject,
        name = name,
        title = title
    )
}