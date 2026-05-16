package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RootBlock
import io.github.thisismeamir.rootkt.format.models.RootRecord
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteBuffer.parseRecord(): RootRecord {
    val key = parseKey()
    val payloadSize = key.dataSize
    val payloadBytes = ByteArray(payloadSize).also { get(it) }

    val block = if (key.isCompressed) {
        val compBlock = ByteBuffer.wrap(payloadBytes)
            .order(ByteOrder.BIG_ENDIAN)
            .parseCompressedBlock()
        RootBlock.Compressed(compBlock)
    } else {
        RootBlock.Raw(payloadBytes)
    }

    return RootRecord(key, block)
}