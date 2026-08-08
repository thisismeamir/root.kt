package io.github.thisismeamir.rootkt.format.parsers.base

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Reads a ROOT TString from the current buffer position, handling
 * both small and large variable-length string descriptors.
 */
fun ByteBuffer.parseTString(): String {
    // Read the initial length flag safely as an unsigned byte
    val initialLength = get().toInt() and 0xFF

    // Determine the true character payload length
    val actualLength = if (initialLength == 255) {
        int // If flagged with 255, read the next 4-byte integer
    } else {
        initialLength
    }

    if (actualLength == 0) return ""

    // Read the string character bytes out of the buffer stream
    val stringBytes = ByteArray(actualLength)
    get(stringBytes)

    return String(stringBytes, StandardCharsets.UTF_8)
}