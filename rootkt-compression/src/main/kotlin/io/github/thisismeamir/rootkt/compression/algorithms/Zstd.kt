package io.github.thisismeamir.rootkt.compression.algorithms

import com.github.luben.zstd.Zstd

fun zstdCompress(data: ByteArray, level: Int = 3): ByteArray =
    Zstd.compress(data, level)

fun zstdDecompress(compressed: ByteArray, uncompressedSize: Int): ByteArray {
    val output = ByteArray(uncompressedSize)
    val n = Zstd.decompress(output, compressed)
    check(n == uncompressedSize.toLong()) { "zstd: expected $uncompressedSize bytes, got $n" }
    return output
}