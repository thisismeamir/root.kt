package io.github.thisismeamir.rootkt.format.models

data class TObjectArray<T>(
    val classInfo: ClassInfo,
    val version: Short,
    val tObject: TObject,
    val fName: String? = null,
    val numberOfObjects: Int,
    val fLowerBound: Int,
    val objects: List<T>
)

data class RawTObjectArray(
    val classInfo: ClassInfo,
    val version: Short,
    val tObject: TObject,
    val fName: String? = null,
    val fLowerBound: Int,
    val numberOfObjects: Int,
    val objects: ByteArray
)
