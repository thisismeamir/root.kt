package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.ClassInfo
import java.nio.ByteBuffer

const val K_NEW_CLASS_TAG = 0xFFFFFFFF.toInt()
const val K_CLASS_MASK = 0x80000000.toInt()

fun ByteBuffer.readClassInfo(): ClassInfo {
    val tagOrIndex = int

    return if (tagOrIndex == K_NEW_CLASS_TAG) {
        // Case A: This is the first time this class appears in the record.
        // It is followed by a standard null-terminated C-string.
        val name = readNullTerminatedString()
        ClassInfo(
            isNewClass = true,
            clIdx = null,
            className = name
        )
    } else {
        // Case B: It's an old class reference tag.
        // Clear out the 0x80000000 flag bit to get the actual byte offset
        val trueOffset = (tagOrIndex and K_CLASS_MASK.inv())
        ClassInfo(
            isNewClass = false,
            clIdx = trueOffset,
            className = null
        )
    }
}

/**
 * Helper to read a classic null-terminated C-string (\0) from the buffer,
 * as specified by the "TStreamerInfo (null terminated)" requirement.
 */
private fun ByteBuffer.readNullTerminatedString(): String {
    val baos = java.io.ByteArrayOutputStream()
    while (hasRemaining()) {
        val b = get()
        if (b == 0.toByte()) break
        baos.write(b.toInt())
    }
    return baos.toString(java.nio.charset.StandardCharsets.UTF_8.name())
}