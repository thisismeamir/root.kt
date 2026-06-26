package io.github.thisismeamir.rootkt.format.models

data class TList<T>(
    val byteCount : Int,
    val version: Short,
    val tObject: TObject,
    val fName : String,
    val numberOfObjects: Int,
    val objects: List<T>
)


data class RawTList (
    val byteCount: Int,
    val version: Short,
    val tObject: TObject,
    val fName : String,
    val numberOfObjects: Int,
    val objects: ByteArray
)
