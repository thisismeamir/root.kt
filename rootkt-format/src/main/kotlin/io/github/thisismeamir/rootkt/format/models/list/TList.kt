package io.github.thisismeamir.rootkt.format.models.list

import io.github.thisismeamir.rootkt.format.models.base.TObject

data class TList<T>(
    val byteCount : Int,
    val version: Short,
    val tObject: TObject,
    val fName : String,
    val numberOfObjects: Int,
    val objects: List<T>
)


