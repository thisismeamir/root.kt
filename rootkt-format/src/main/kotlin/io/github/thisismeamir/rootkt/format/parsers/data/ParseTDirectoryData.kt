package io.github.thisismeamir.rootkt.format.parsers.data

import io.github.thisismeamir.rootkt.format.models.directory.TDirectoryData
import io.github.thisismeamir.rootkt.format.models.base.TKey
import java.nio.ByteBuffer
fun ByteBuffer.parseTDirectoryData(): TDirectoryData {
    val version = short
    val datimeC = int
    val datimeM = int
    val nbytesKeys = int
    val nbytesName = int

    val large = version > 1000
    val seekDir = if (large) long else int.toLong()
    val seekParent = if (large) long else int.toLong()
    val seekKeys = if (large) long else int.toLong()

    val versiondir = version % 1000
    val uuid = if (versiondir > 1) ByteArray(16).also { get(it) } else null

    return TDirectoryData(version, datimeC, datimeM, nbytesKeys, nbytesName, seekDir, seekParent, seekKeys, uuid)
}

fun List<TKey>.buildDirectoryTree(rootSeekKey: Long): Map<Long, List<TKey>> =
    groupBy { it.seekPdir }