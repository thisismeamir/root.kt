package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.streamer.models.types.StreamerType
import java.nio.ByteBuffer

fun ByteBuffer.parseBuiltInTypes(type: StreamerType.BuiltIn): Any = when (type) {
    StreamerType.BuiltIn.Char -> get()
    StreamerType.BuiltIn.UChar -> (get().toInt() and 0xFF)
    StreamerType.BuiltIn.Short -> short
    StreamerType.BuiltIn.UShort -> (short.toInt() and 0xFFFF)
    StreamerType.BuiltIn.Int -> int
    StreamerType.BuiltIn.UInt -> int
    StreamerType.BuiltIn.Long -> long
    StreamerType.BuiltIn.ULong -> long
    StreamerType.BuiltIn.Long64 -> long
    StreamerType.BuiltIn.ULong64 -> long
    StreamerType.BuiltIn.Float -> float
    StreamerType.BuiltIn.Double -> double
    StreamerType.BuiltIn.Double32 -> float.toDouble()
    StreamerType.BuiltIn.Float16 -> short  // TODO: real Float16 decode (ROOT-specific compression), placeholder raw bits for now
    StreamerType.BuiltIn.Bool -> get() != 0.toByte()
}