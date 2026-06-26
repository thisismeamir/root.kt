package io.github.thisismeamir.rootkt.format.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.toByteBuffer(): ByteBuffer {
    return ByteBuffer.wrap(this).order(ByteOrder.BIG_ENDIAN)
}
