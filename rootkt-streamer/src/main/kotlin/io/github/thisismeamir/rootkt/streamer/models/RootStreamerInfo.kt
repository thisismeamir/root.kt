package io.github.thisismeamir.rootkt.streamer.models

data class RootStreamerInfo(
    val className: String,
    val checksum: Long,
    val classVersion: Int,
    val elements: List<RootStreamerElement>
)