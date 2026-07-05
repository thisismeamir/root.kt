package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.TKey
import java.nio.ByteBuffer

/**
 * Parses a single [TKey] from the current position of this [ByteBuffer].
 *
 * Every data record in a ROOT file is preceded by a TKey, which is a fixed-layout
 * header describing the record's size, location, class, name, and compression state.
 * This function reads that header sequentially, advancing the buffer position to
 * the start of the data payload (i.e. [TKey.seekKey] + [TKey.keyLen]).
 *
 * The key structure is never compressed. However, the layout is not entirely fixed:
 * if the key is located past the 32-bit file boundary (>2 GB), [TKey.seekKey]
 * and [TKey.seekPdir] are each 8 bytes on disk instead of 4. This is detected
 * per-key by checking [TKey.version] > 1000, independently of whether the file
 * header itself indicates a large file.
 *
 * The three string fields ([TKey.className], [TKey.name], [TKey.title])
 * are Pascal-style strings: a single unsigned length byte followed by that many
 * UTF-8 characters. An empty string is represented as a single zero byte.
 *
 * @receiver A [ByteBuffer] positioned at the start of a TKey record (i.e. at the
 *   first byte of [TKey.nbytes]).
 * @return A fully populated [TKey].
 *
 * @see TKey for the meaning of each parsed field.
 */
fun ByteBuffer.parseKey(): TKey {
    val nbytes  = int
    val version = short
    val objLen  = int
    val datime  = int
    val keyLen  = short
    val cycle   = short

    // Keys past the 32-bit file boundary use 8-byte offsets for seekKey and seekPdir.
    // This is signalled per-key by version > 1000, not by the file-level large flag.
    val large    = version > 1000
    val seekKey  = if (large) long else int.toLong()
    val seekPdir = if (large) long else int.toLong()

    val className = readTString()
    val name = readTString()
    val title = readTString()

    return TKey(
        nbytes   = nbytes,
        version  = version,
        objLen   = objLen,
        datime   = datime,
        keyLen   = keyLen,
        cycle    = cycle,
        seekKey  = seekKey,
        seekPdir = seekPdir,
        // Class name identifies which streamer to use for deserialization (e.g. "TH1F").
        className = className,
        // Name and cycle together uniquely identify this object within its directory.
        name      = name,
        title     = title
    )
}