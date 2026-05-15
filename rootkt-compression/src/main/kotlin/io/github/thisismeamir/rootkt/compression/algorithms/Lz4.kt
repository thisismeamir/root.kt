package io.github.thisismeamir.rootkt.compression.algorithms

import net.jpountz.lz4.LZ4Factory

private val factory = LZ4Factory.fastestInstance()

fun lz4Compress(data: ByteArray, level: Int = 6): ByteArray {
    val compressor = if (level >= 9) factory.highCompressor() else factory.fastCompressor()
    return compressor.compress(data)
}

fun lz4Decompress(compressed: ByteArray, uncompressedSize: Int): ByteArray =
    factory.fastDecompressor().decompress(compressed, uncompressedSize)