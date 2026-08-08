package io.github.thisismeamir.rootkt.format.parsers.data

import io.github.thisismeamir.rootkt.format.models.block.Block
import io.github.thisismeamir.rootkt.format.models.base.Record
import io.github.thisismeamir.rootkt.format.parsers.base.parseKey
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