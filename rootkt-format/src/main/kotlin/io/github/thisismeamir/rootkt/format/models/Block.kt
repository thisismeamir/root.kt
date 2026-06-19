package io.github.thisismeamir.rootkt.format.models

import io.github.thisismeamir.rootkt.compression.models.CompressedBlock
import io.github.thisismeamir.rootkt.compression.services.Decompressor

sealed class Block {
    abstract val rawSize: Int
    abstract val data: ByteArray  // always the uncompressed bytes, lazily resolved

    data class Compressed(val block: CompressedBlock) : Block() {
        override val rawSize: Int get() = block.uncompressedSize
        override val data: ByteArray by lazy { Decompressor.decompress(block) }
    }

    data class Raw(override val data: ByteArray) : Block() {
        override val rawSize: Int get() = data.size
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Raw

            if (!data.contentEquals(other.data)) return false
            if (rawSize != other.rawSize) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.contentHashCode()
            result = 31 * result + rawSize
            return result
        }
    }
}