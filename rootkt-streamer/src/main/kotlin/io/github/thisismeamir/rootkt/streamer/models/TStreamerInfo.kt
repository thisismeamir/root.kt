package io.github.thisismeamir.rootkt.streamer.models

import io.github.thisismeamir.rootkt.format.models.ClassInfo
import io.github.thisismeamir.rootkt.format.models.TNamed
import io.github.thisismeamir.rootkt.format.models.TObjectArray

data class TStreamerInfo(
    val classInfo: ClassInfo,
    val version: Short,
    val tNamed: TNamed,
    val fCheckSum: Int,
    val fClassVersion: Int,
    // Just For debugging we change this to ByteArray, otherwise it should be TStreamerElement
    val objectArray: TObjectArray<TStreamerElement>
)