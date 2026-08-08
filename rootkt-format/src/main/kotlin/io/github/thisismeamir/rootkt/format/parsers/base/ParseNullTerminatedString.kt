package io.github.thisismeamir.rootkt.format.parsers.base

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Helper to read a classic null-terminated C-string (\0) from the buffer,
 * as specified by the "TStreamerInfo (null terminated)" requirement.
 */
fun ByteBuffer.parseNullTerminatedString(): String {
    val baos = ByteArrayOutputStream()
    while (hasRemaining()) {
        val b = get()
        if (b == 0.toByte()) break
        baos.write(b.toInt())
    }
    return baos.toString(StandardCharsets.UTF_8.name())
}