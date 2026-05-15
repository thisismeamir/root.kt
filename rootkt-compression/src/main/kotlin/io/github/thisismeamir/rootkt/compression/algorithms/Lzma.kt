package io.github.thisismeamir.rootkt.compression.algorithms

import org.tukaani.xz.LZMA2Options
import org.tukaani.xz.XZInputStream
import org.tukaani.xz.XZOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun lzmaCompress(data: ByteArray, level: Int = 6): ByteArray {
    val output = ByteArrayOutputStream()
    XZOutputStream(output, LZMA2Options(level)).use { it.write(data) }
    return output.toByteArray()
}

fun lzmaDecompress(compressed: ByteArray, uncompressedSize: Int): ByteArray {
    val output = ByteArray(uncompressedSize)
    XZInputStream(ByteArrayInputStream(compressed)).use { it.read(output) }
    return output
}