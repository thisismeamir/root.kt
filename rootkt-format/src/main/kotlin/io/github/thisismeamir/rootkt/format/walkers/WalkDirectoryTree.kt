package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.TDirectory
import io.github.thisismeamir.rootkt.format.models.TDirectoryNode
import io.github.thisismeamir.rootkt.format.models.TKey
import java.nio.ByteBuffer


fun ByteBuffer.walkDirectory(keyPosition: Int): TDirectory {
    // TODO Assumes the given keyposition is for a directory
    val key = this parseKeyFrom keyPosition

    val data = readTDirectoryData()
    return TDirectory(key, data)
}

fun ByteBuffer.readKeysAt(seekKeys: Long): List<TKey> {
    this.position(seekKeys.toInt())
    this.parseKey()          // wrapper key describing this directory/file
    val nkeys = this.int
    return List(nkeys) { this.parseKey() }
}

fun TDirectory.walkSubDirectories(buf: ByteBuffer): List<TDirectory> =
    readOwnKeys(buf)
        .filter { it.className == "TDirectory" || it.className == "TDirectoryFile" }
        .map { buf.walkDirectory(it.seekKey.toInt()) }

fun TDirectory.objects(buf: ByteBuffer): List<TKey> =
    readOwnKeys(buf)
        .filterNot { it.className == "TDirectory" || it.className == "TDirectoryFile" }


fun TDirectory.readOwnKeys(buf: ByteBuffer): List<TKey> {
    return buf.readKeysAt(this.data.seekKeys)
}

fun TDirectory.toNode(buf: ByteBuffer): TDirectoryNode {
    val (dirKeys, objKeys) = readOwnKeys(buf)
        .partition { it.className == "TDirectory" || it.className == "TDirectoryFile" }
    return TDirectoryNode(
        key = key,
        data = data,
        objectKeys = objKeys,
        children = dirKeys.map { buf.walkDirectory(it.seekKey.toInt()).toNode(buf) }
    )
}