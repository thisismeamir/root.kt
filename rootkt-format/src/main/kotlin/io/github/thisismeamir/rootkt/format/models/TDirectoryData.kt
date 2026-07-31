package io.github.thisismeamir.rootkt.format.models

data class TDirectoryData(
    val version: Short,
    val datimeC: Int,
    val datimeM: Int,
    val nbytesKeys: Int,
    val nbytesName: Int,
    val seekDir: Long,
    val seekParent: Long,
    val seekKeys: Long,
    val uuid: ByteArray?
)