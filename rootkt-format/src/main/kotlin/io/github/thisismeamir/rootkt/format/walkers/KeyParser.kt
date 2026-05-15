package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RootTKey
import java.nio.ByteBuffer

/**
 * Parses a single [RootTKey] from the current position of this [ByteBuffer].
 *
 * Every data record in a ROOT file is preceded by a TKey, which is a fixed-layout
 * header describing the record's size, location, class, name, and compression state.
 * This function reads that header sequentially, advancing the buffer position to
 * the start of the data payload (i.e. [RootTKey.seekKey] + [RootTKey.keyLen]).
 *
 * The key structure is never compressed. However, the layout is not entirely fixed:
 * if the key is located past the 32-bit file boundary (>2 GB), [RootTKey.seekKey]
 * and [RootTKey.seekPdir] are each 8 bytes on disk instead of 4. This is detected
 * per-key by checking [RootTKey.version] > 1000, independently of whether the file
 * header itself indicates a large file.
 *
 * The three string fields ([RootTKey.className], [RootTKey.name], [RootTKey.title])
 * are Pascal-style strings: a single unsigned length byte followed by that many
 * UTF-8 characters. An empty string is represented as a single zero byte.
 *
 * @receiver A [ByteBuffer] positioned at the start of a TKey record (i.e. at the
 *   first byte of [RootTKey.nbytes]).
 * @return A fully populated [RootTKey].
 *
 * @see RootTKey for the meaning of each parsed field.
 */
fun ByteBuffer.parseKey(): RootTKey {
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

    /**
     * Reads a ROOT Pascal-style string from the buffer.
     *
     * ROOT stores short strings as a 1-byte unsigned length followed by that many
     * UTF-8 bytes. The length byte is masked with 0xFF to treat it as unsigned,
     * since Kotlin's [ByteBuffer.get] returns a signed Byte — without the mask,
     * strings longer than 127 bytes would produce a negative length.
     */
    fun ByteBuffer.readRootString(): String {
        val len = get().toInt() and 0xFF
        if (len == 0) return ""
        return ByteArray(len).also { get(it) }.toString(Charsets.UTF_8)
    }

    return RootTKey(
        nbytes   = nbytes,
        version  = version,
        objLen   = objLen,
        datime   = datime,
        keyLen   = keyLen,
        cycle    = cycle,
        seekKey  = seekKey,
        seekPdir = seekPdir,
        // Class name identifies which streamer to use for deserialization (e.g. "TH1F").
        className = readRootString(),
        // Name and cycle together uniquely identify this object within its directory.
        name      = readRootString(),
        title     = readRootString()
    )
}