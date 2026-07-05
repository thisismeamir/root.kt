package io.github.thisismeamir.rootkt.streamer.service

import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.walkers.parseRecord
import io.github.thisismeamir.rootkt.format.walkers.parseRootHeader
import io.github.thisismeamir.rootkt.format.walkers.readRawTList
import io.github.thisismeamir.rootkt.streamer.walkers.readAsTStreamerInfos
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteBuffer.readStreamer(): StreamerRegistry {
    val buf = this.order(ByteOrder.BIG_ENDIAN)
    val header = buf.parseRootHeader()

    val record =
        buf.position(header.seekInfo.toInt())
        .parseRecord()

    val streamers =
        record
            .block
            .data
            .toByteBuffer()
            .readRawTList()
            .readAsTStreamerInfos()
    return StreamerRegistry(
        key = record.key,
        streamers = streamers
    )
}