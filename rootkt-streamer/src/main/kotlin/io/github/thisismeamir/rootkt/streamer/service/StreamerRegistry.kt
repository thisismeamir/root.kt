package io.github.thisismeamir.rootkt.streamer.service

import io.github.thisismeamir.rootkt.format.models.Record
import io.github.thisismeamir.rootkt.format.models.TKey
import io.github.thisismeamir.rootkt.format.models.TList
import io.github.thisismeamir.rootkt.streamer.models.TStreamerInfo

class StreamerRegistry(
    val key: TKey,
    val streamers: TList<TStreamerInfo>
) {
    private val classesByOffset = mutableMapOf<Int, String>()
    private val resolvedByClIdx = mutableMapOf<Int, String>()

    init {
        collectNewClassTags()
        resolveReferences()
    }

    private fun collectNewClassTags() {
        var index = 0
        streamers.objects.forEach { info ->
            if (info.classInfo.isNewClass && info.classInfo.className != null) {
                val name = info.classInfo.className
                if (info.classInfo.isNewClass && name != null) {
                    classesByOffset[index] = name
                }
                }
            index++
            val arrayInfo = info.objectArray.classInfo
            if (arrayInfo.isNewClass && arrayInfo.className != null) {
                val name = arrayInfo.className
                if (name != null) {
                    classesByOffset[index] = name
                }
            }
            index++
        }
    }

    private fun resolveReferences() {
        var index = 0
        streamers.objects.forEach { info ->
            if (!info.classInfo.isNewClass) {
                info.classInfo.clIdx?.let { resolvedByClIdx[it] = classesByOffset[it] ?: "" }
            }
            index++
            val arrayInfo = info.objectArray.classInfo
            if (!arrayInfo.isNewClass) {
                arrayInfo.clIdx?.let { resolvedByClIdx[it] = classesByOffset[it] ?: "" }
            }
            index++
        }
    }

    fun resolveClassName(clIdx: Int): String? = resolvedByClIdx[clIdx]
}