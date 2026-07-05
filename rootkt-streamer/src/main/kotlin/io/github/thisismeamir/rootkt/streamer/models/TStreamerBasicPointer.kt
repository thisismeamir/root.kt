package io.github.thisismeamir.rootkt.streamer.models

import io.github.thisismeamir.rootkt.format.models.TNamed

data class TStreamerBasicPointer(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String,
    val fCountVersion: Int,
    val fCountName: String,
    val fCountClass: String
) : TStreamerElement