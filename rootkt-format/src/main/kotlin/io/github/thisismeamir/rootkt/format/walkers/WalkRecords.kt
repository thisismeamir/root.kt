package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.Block
import io.github.thisismeamir.rootkt.format.models.Record
import java.nio.ByteBuffer

fun ByteBuffer.walkRecords(begin: Int, end: Long): List<Record> =
    walkKeys(begin, end).map { key ->
        position(key.seekKey.toInt() + key.keyLen)
        val payloadBytes = ByteArray(key.dataSize).also { get(it) }
        val block = if (key.isCompressed) {
            Block.Compressed(
                ByteBuffer.wrap(payloadBytes).parseCompressedBlock()
            )
        } else {
            Block.Raw(payloadBytes)
        }
        Record(key, block)
    }