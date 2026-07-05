package io.github.thisismeamir.rootkt.streamer.models

import io.github.thisismeamir.rootkt.format.models.TNamed

sealed interface TStreamerElement{
    val byteCount: Int
    val version: Short
    val named: TNamed
    val fType: StreamerType
    val fSize: Int
    val fArrayLength: Int
    val fArrayDim: Int
    val fMaxIndex: List<Int>
    val fTypeName: String
}