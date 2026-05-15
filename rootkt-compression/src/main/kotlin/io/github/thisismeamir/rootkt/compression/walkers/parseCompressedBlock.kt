package io.github.thisismeamir.rootkt.compression.walkers

import io.github.thisismeamir.rootkt.compression.models.CompressedBlock
import io.github.thisismeamir.rootkt.compression.models.CompressionType
import java.nio.ByteBuffer

fun ByteBuffer.parseCompressedBlock(): CompressedBlock {
    val magic = ByteArray(2).also { get(it) }
    val algorithm = CompressionType.entries.find { it.magic == String(magic) }
        ?: error("Unknown compression magic: ${String(magic)}")
    val level = get()
    // 3-byte little-endian integers
    val compressedSize   = (get().toInt() and 0xFF) or ((get().toInt() and 0xFF) shl 8) or ((get().toInt() and 0xFF) shl 16)
    val uncompressedSize = (get().toInt() and 0xFF) or ((get().toInt() and 0xFF) shl 8) or ((get().toInt() and 0xFF) shl 16)
    val data = ByteArray(compressedSize).also { get(it) }
    return CompressedBlock(algorithm, level, compressedSize, uncompressedSize, data)
}