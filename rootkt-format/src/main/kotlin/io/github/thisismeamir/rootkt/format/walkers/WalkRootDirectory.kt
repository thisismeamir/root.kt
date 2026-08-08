package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.base.FileHeader
import io.github.thisismeamir.rootkt.format.models.directory.TDirectoryRoot
import io.github.thisismeamir.rootkt.format.parsers.data.parseTDirectoryData
import java.nio.ByteBuffer

fun ByteBuffer.walkRootDirectory(header: FileHeader): TDirectoryRoot {
    this.position(header.begin + header.nbytesName)
    val rootData = this.parseTDirectoryData()

    val topKeys = this.readKeysAt(rootData.seekKeys)
    val (dirKeys, objKeys) = topKeys.partition { it.className == "TDirectory" || it.className == "TDirectoryFile" }

    return TDirectoryRoot(
        header = header,
        data = rootData,
        objectKeys = objKeys,
        children = dirKeys.map { this.walkDirectory(it.seekKey.toInt()).toNode(this) }
    )
}