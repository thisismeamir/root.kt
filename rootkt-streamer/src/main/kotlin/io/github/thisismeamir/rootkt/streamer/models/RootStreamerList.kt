package io.github.thisismeamir.rootkt.streamer.models

data class RootStreamerList(
    val infos: List<RootStreamerInfo>
) {
    private val byName = infos.groupBy { it.className }

    fun find(className: String): RootStreamerInfo? =
        byName[className]?.maxByOrNull { it.classVersion }

    fun find(className: String, version: Int): RootStreamerInfo? =
        byName[className]?.find { it.classVersion == version }
}