package io.github.thisismeamir.rootkt.streamer.conversions

import io.github.thisismeamir.rootkt.streamer.models.types.StreamerType

fun Int.toStreamerType(): StreamerType {
    val typename = this
    return when {
        typename == 0  -> StreamerType.Object.Base
        typename == 6  -> StreamerType.Counter
        typename == 15 -> StreamerType.BitMask
        typename == 65 -> StreamerType.Object.TString
        typename == 66 -> StreamerType.Object.TObject
        typename == 67 -> StreamerType.Object.TNamed
        typename == 61 -> StreamerType.Object.Derived
        typename == 62 -> StreamerType.Object.Any
        typename == 63 -> StreamerType.ObjectPointer.NonNull
        typename == 64 -> StreamerType.ObjectPointer.Nullable
        typename == 68 -> StreamerType.ObjectPointer.AnyNonNull
        typename == 69 -> StreamerType.ObjectPointer.AnyNullable
        typename == 70 -> StreamerType.ObjectPointer.AnyNoVT
        typename == 71 -> StreamerType.STLPointer
        typename == 300 -> StreamerType.STL
        typename == 365 -> StreamerType.Object.TString
        typename == 500 -> StreamerType.STL
        typename == 501 -> StreamerType.ObjectArray
        // Pointers to core types (kOffsetP = 40 + base type, base in 0..19)
        typename in 41..59 -> {
            val baseBuiltIn = (typename - 40).toStreamerType() as StreamerType.BuiltIn
            StreamerType.Pointer(of = baseBuiltIn)
        }
        // Arrays of core types (kOffsetL = 20 + base type, base in 0..19)
        typename in 21..39 -> {
            val baseBuiltIn = (typename - 20).toStreamerType() as StreamerType.BuiltIn
            StreamerType.Array(of = baseBuiltIn)
        }
        // Basic scalar primitives
        else -> when (typename) {
            1  -> StreamerType.BuiltIn.Char
            2  -> StreamerType.BuiltIn.Short
            3  -> StreamerType.BuiltIn.Int
            4  -> StreamerType.BuiltIn.Long
            5  -> StreamerType.BuiltIn.Float
            8  -> StreamerType.BuiltIn.Double
            9  -> StreamerType.BuiltIn.Double32
            10 -> StreamerType.BuiltIn.Char // kLegacyChar
            11 -> StreamerType.BuiltIn.UChar
            12 -> StreamerType.BuiltIn.UShort
            13 -> StreamerType.BuiltIn.UInt
            14 -> StreamerType.BuiltIn.ULong
            16 -> StreamerType.BuiltIn.Long64
            17 -> StreamerType.BuiltIn.ULong64
            18 -> StreamerType.BuiltIn.Bool
            19 -> StreamerType.BuiltIn.Float16
            else -> throw IllegalArgumentException("Unrecognized binary ROOT data type code definition: $typename")
        }
    }
}