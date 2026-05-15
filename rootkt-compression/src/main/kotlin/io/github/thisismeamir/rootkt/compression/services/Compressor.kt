package io.github.thisismeamir.rootkt.compression.services

import io.github.thisismeamir.rootkt.compression.algorithms.lz4Compress
import io.github.thisismeamir.rootkt.compression.algorithms.lzmaCompress
import io.github.thisismeamir.rootkt.compression.algorithms.zlibCompress
import io.github.thisismeamir.rootkt.compression.algorithms.zstdCompress
import io.github.thisismeamir.rootkt.compression.models.CompressedBlock
import io.github.thisismeamir.rootkt.compression.models.CompressionType

object Compressor {
    fun compress(bytes: ByteArray, level: Int, compressionAlgorithm: CompressionType): CompressedBlock {
        val compressedData = when (compressionAlgorithm) {
        CompressionType.ZLIB -> zlibCompress(bytes, level)
        CompressionType.LZ4  -> lz4Compress(bytes, level)
        CompressionType.ZSTD -> zstdCompress(bytes, level)
        CompressionType.LZMA -> lzmaCompress(bytes, level)
        CompressionType.NONE -> bytes
    }
        return CompressedBlock(
            algorithm = compressionAlgorithm,
            level = level.toByte(),
            compressedSize = compressedData.size,
            uncompressedSize = bytes.size,
            data = compressedData
        )
    }
}