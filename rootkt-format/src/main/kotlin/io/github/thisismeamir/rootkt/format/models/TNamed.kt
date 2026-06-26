package io.github.thisismeamir.rootkt.format.models

data class TNamed(
    val byteCount : Int,
    val version : Short,
    val obj : TObject,
    val name: String,
    val title: String
)
