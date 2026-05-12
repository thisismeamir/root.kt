package com.thisismeamir.rootkt.format

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class RootFileHeader(
    val version: Int,
    val begin: Int,
    val end: Long,
    val seekFree: Long,
    val nbytesFree: Int,
    val nfree: Int,
    val nbytesName: Int,
    val units: Byte,
    val compress: Int,
    val seekInfo: Long,
    val nbytesInfo: Int,
    val uuid: ByteArray  // 16 bytes
) {
    val isLargeFile: Boolean get() = version >= 1_000_000
}


fun ByteBuffer.parseRootHeader(): RootFileHeader {
    order(ByteOrder.BIG_ENDIAN)
    val magic = ByteArray(4).also { get(it) }
    require(String(magic) == "root") { "Not a ROOT file" }

    val version = int
    val large = version >= 1_000_000
    return RootFileHeader(
        version    = version,
        begin      = int,
        end        = if (large) long else int.toLong(),
        seekFree   = if (large) long else int.toLong(),
        nbytesFree = int,
        nfree      = int,
        nbytesName = int,
        units      = get(),
        compress   = int,
        seekInfo   = if (large) long else int.toLong(),
        nbytesInfo = int,
        uuid       = ByteArray(16).also { position(47); get(it) }
    )
}