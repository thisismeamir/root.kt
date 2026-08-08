package io.github.thisismeamir.rootkt.format.models.objectarray

import io.github.thisismeamir.rootkt.format.models.base.TObject
import io.github.thisismeamir.rootkt.format.models.base.ClassInfo

data class TObjectArray<T>(
    val classInfo: ClassInfo,
    val version: Short,
    val tObject: TObject,
    val fName: String? = null,
    val numberOfObjects: Int,
    val fLowerBound: Int,
    val objects: List<T>
)

