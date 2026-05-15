package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RootTKey
import java.nio.ByteBuffer

/**
 * Walks all [RootTKey] records in this [ByteBuffer] sequentially from [begin] to [end],
 * returning them as an ordered list.
 *
 * ROOT files are a flat sequence of consecutive data records. Each record is exactly
 * [RootTKey.nbytes] bytes long, so the next record always starts at the current
 * offset + [RootTKey.nbytes]. This function exploits that invariant to traverse the
 * entire file in a single linear pass without needing the KeysList index.
 *
 * This is useful for:
 * - File recovery (reading records even if the KeysList is corrupt or missing)
 * - Listing all objects including internal ROOT records (TFile, StreamerInfo, etc.)
 * - Verifying file integrity by comparing the walk against the KeysList
 *
 * The walk stops early if a non-positive [RootTKey.nbytes] is encountered. A negative
 * value signals a free segment — a gap left behind by a deleted or resized record.
 * ROOT marks these gaps by overwriting their first 4 bytes with the negated gap size,
 * so a negative fNbytes is not an error; it simply means there is no key here.
 *
 * Note: this walk includes ROOT's internal bookkeeping keys (TFile, KeysList,
 * FreeSegments, StreamerInfo) which do not appear in the user-visible KeysList.
 * Callers that want only user data should filter by [RootTKey.className].
 *
 * @receiver A [ByteBuffer] wrapping the full contents of a ROOT file, in big-endian order.
 * @param begin Byte offset of the first TKey to read. Should be [RootFileHeader.begin]
 *   (always 100 in modern ROOT files) to walk from the very first record.
 * @param end Byte offset of the first byte past the last record. Should be
 *   [RootFileHeader.end] (the file size) to walk the entire file.
 * @return A list of all [RootTKey] records found between [begin] and [end], in file order.
 */
fun ByteBuffer.walkKeys(begin: Int, end: Long): List<RootTKey> {
    val keys = mutableListOf<RootTKey>()
    var offset = begin.toLong()

    while (offset < end) {
        // Peek at fNbytes without advancing the position, then reposition for parsing.
        // A non-positive value means this is a free segment (deleted record gap),
        // not a valid TKey — stop the walk immediately.
        val nbytes = this.getInt(offset.toInt())
        if (nbytes <= 0) break

        this.position(offset.toInt())
        val key = this.parseKey()
        keys.add(key)

        // Advance by the full record size to reach the next TKey.
        // nbytes covers both the key structure and the (compressed) data payload.
        offset += nbytes
    }

    return keys
}