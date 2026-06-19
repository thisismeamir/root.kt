package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.Block
import io.github.thisismeamir.rootkt.format.models.Record
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteBuffer.parseRecord(): Record {
    val key = parseKey()
    val payloadSize = key.dataSize
    val payloadBytes = ByteArray(payloadSize).also { get(it) }

    val block = if (key.isCompressed) {
        val compBlock = ByteBuffer.wrap(payloadBytes)
            .order(ByteOrder.BIG_ENDIAN)
            .parseCompressedBlock()
        Block.Compressed(compBlock)
    } else {
        Block.Raw(payloadBytes)
    }

    return Record(key, block)
}