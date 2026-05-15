package io.github.thisismeamir.rootkt.compression.models

import io.github.thisismeamir.rootkt.compression.services.Decompressor

data class CompressedBlock(
    val algorithm: CompressionType,
    val level: Byte,
    val compressedSize: Int,
    val uncompressedSize: Int,
    val data: ByteArray         // compressedSize bytes, ready to decompress
) {

    val decompressed: ByteArray by lazy {
        Decompressor.decompress(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompressedBlock

        if (level != other.level) return false
        if (compressedSize != other.compressedSize) return false
        if (uncompressedSize != other.uncompressedSize) return false
        if (algorithm != other.algorithm) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level
        result = (31 * result + compressedSize).toByte()
        result = (31 * result + uncompressedSize).toByte()
        result = (31 * result + algorithm.hashCode()).toByte()
        result = (31 * result + data.contentHashCode()).toByte()
        return result.toInt()
    }
}

