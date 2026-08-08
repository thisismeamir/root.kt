package io.github.thisismeamir.rootkt.streamer.models.streamerinfo

import io.github.thisismeamir.rootkt.format.models.base.ClassInfo
import io.github.thisismeamir.rootkt.format.models.base.TNamed
import io.github.thisismeamir.rootkt.format.models.objectarray.TObjectArray
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerElement

data class TStreamerInfo(
    val classInfo: ClassInfo,
    val version: Short,
    val tNamed: TNamed,
    val fCheckSum: Int,
    val fClassVersion: Int,
    val objectArray: TObjectArray<TStreamerElement>
)