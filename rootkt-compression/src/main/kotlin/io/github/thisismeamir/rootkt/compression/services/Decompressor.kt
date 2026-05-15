package io.github.thisismeamir.rootkt.compression.services

import io.github.thisismeamir.rootkt.compression.algorithms.lz4Decompress
import io.github.thisismeamir.rootkt.compression.algorithms.lzmaDecompress
import io.github.thisismeamir.rootkt.compression.algorithms.zlibDecompress
import io.github.thisismeamir.rootkt.compression.algorithms.zstdDecompress
import io.github.thisismeamir.rootkt.compression.models.CompressedBlock
import io.github.thisismeamir.rootkt.compression.models.CompressionType

object Decompressor {

    fun decompress(block: CompressedBlock): ByteArray = when (block.algorithm) {
        CompressionType.ZLIB -> zlibDecompress(block.data, block.uncompressedSize)
        CompressionType.LZ4  -> lz4Decompress(block.data, block.uncompressedSize)
        CompressionType.ZSTD -> zstdDecompress(block.data, block.uncompressedSize)
        CompressionType.LZMA -> lzmaDecompress(block.data, block.uncompressedSize)
        CompressionType.NONE -> block.data
    }
}