package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RootBlock
import io.github.thisismeamir.rootkt.format.models.RootRecord
import java.nio.ByteBuffer

fun ByteBuffer.walkRecords(begin: Int, end: Long): List<RootRecord> =
    walkKeys(begin, end).map { key ->
        position(key.seekKey.toInt() + key.keyLen)
        val payloadBytes = ByteArray(key.dataSize).also { get(it) }
        val block = if (key.isCompressed) {
            RootBlock.Compressed(
                ByteBuffer.wrap(payloadBytes).parseCompressedBlock()
            )
        } else {
            RootBlock.Raw(payloadBytes)
        }
        RootRecord(key, block)
    }