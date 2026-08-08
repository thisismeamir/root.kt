package io.github.thisismeamir.rootkt.format.models.objectarray

import io.github.thisismeamir.rootkt.format.models.base.TObject
import io.github.thisismeamir.rootkt.format.models.base.ClassInfo

data class RawTObjectArray(
    val classInfo: ClassInfo,
    val version: Short,
    val tObject: TObject,
    val fName: String,
    val fLowerBound: Int,
    val numberOfObjects: Int,
    val objects: ByteArray,
    val objectsBaseOffset: Int
)