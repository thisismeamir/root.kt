package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.FileHEader
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Parses a [FileHEader] from the current position of this [ByteBuffer].
 *
 * This function reads the fixed 100-byte file header that begins every ROOT file.
 * The header is always at byte offset 0, always big-endian, and never compressed.
 * The buffer's position should be 0 when this is called; it will be advanced to
 * byte 63 after reading (end of the fixed header fields, before the reserved padding).
 *
 * The header layout differs slightly between standard files and large files (>2 GB).
 * In large files, [FileHEader.end], [FileHEader.seekFree], and
 * [FileHEader.seekInfo] are stored as 8-byte integers rather than 4-byte integers.
 * This is detected automatically by checking if [FileHEader.version] >= 1,000,000.
 *
 * The UUID is always read from the fixed absolute position 47, regardless of the
 * large-file layout, because the reserved padding region (bytes 63–99) ensures
 * there is always room for the 64-bit fields without shifting the UUID.
 *
 * @receiver A [ByteBuffer] wrapping at least 100 bytes of a ROOT file, positioned at 0.
 * @return A fully populated [FileHEader].
 * @throws IllegalArgumentException if the first 4 bytes are not the magic string "root",
 *   indicating this is not a valid ROOT file.
 */
fun ByteBuffer.parseRootHeader(): FileHEader {
    order(ByteOrder.BIG_ENDIAN)

    // Every ROOT file begins with the 4-byte magic string "root" (lowercase).
    // This is the primary signal that the file is a valid ROOT file.
    val magic = ByteArray(4).also { get(it) }
    require(String(magic) == "root") { "Not a ROOT file" }

    val version = int

    // When version >= 1,000,000 the file exceeds 2 GB and offset fields are 8 bytes.
    // ROOT signals this by adding 1,000,000 to the actual format version number.
    val large = version >= 1_000_000

    return FileHEader(
        version    = version,
        begin      = int,

        // fEND: last used byte in the file. Equals the file size in bytes.
        // 4 bytes in standard files, 8 bytes in large files.
        end        = if (large) long else int.toLong(),

        // fSeekFree: offset of the FreeSegments record, which tracks deleted/reusable gaps.
        seekFree   = if (large) long else int.toLong(),

        nbytesFree = int,
        nfree      = int,
        nbytesName = int,
        units      = get(),
        compress   = int,

        // fSeekInfo: offset of the StreamerInfo record — the file's embedded class schema.
        // Must be read to deserialize any non-core object stored in the file.
        seekInfo   = if (large) long else int.toLong(),

        nbytesInfo = int,

        // UUID is always at absolute byte position 47, after the fixed scalar fields.
        // We seek explicitly rather than reading sequentially because the preceding
        // fields have variable width (4 vs 8 bytes) in large files.
        uuid       = ByteArray(16).also { position(47); get(it) }
    )
}