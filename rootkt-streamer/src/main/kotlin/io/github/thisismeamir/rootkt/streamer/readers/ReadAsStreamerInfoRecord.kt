package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.format.models.base.Record
import io.github.thisismeamir.rootkt.format.utils.toByteBuffer
import io.github.thisismeamir.rootkt.format.parsers.data.parseRawTList
import io.github.thisismeamir.rootkt.format.service.ClassResolver

fun Record.readAsStreamerInfoRecord() {
        val buffer = this.block.data.toByteBuffer()
        val clIdxShift = this.key.keyLen + 2
        val classResolver = ClassResolver(clIdxShift)
        buffer
            .parseRawTList()
            .readAsTStreamerInfos(classResolver)
}