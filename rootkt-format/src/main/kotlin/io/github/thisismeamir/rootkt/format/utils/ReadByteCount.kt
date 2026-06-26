package io.github.thisismeamir.rootkt.format.utils


import java.nio.ByteBuffer

/**
 * ROOT constant mask used to indicate that an object header includes a byte count descriptor.
 */
const val K_BYTE_COUNT_MASK = 0x40000000

/**
 * Reads a 4-byte integer and strips the ROOT byte count mask flag if present,
 * returning the true physical byte length.
 */
fun ByteBuffer.readByteCount(): Int {
    val rawByteCount = int
    return if ((rawByteCount and K_BYTE_COUNT_MASK) != 0) {
        rawByteCount and 0x3FFFFFFF
    } else {
        rawByteCount
    }
}