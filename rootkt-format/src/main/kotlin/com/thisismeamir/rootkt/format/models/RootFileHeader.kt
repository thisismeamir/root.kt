package com.thisismeamir.rootkt.format.models

/**
 * Represents the fixed-length (100 bytes) header of a ROOT file.
 *
 * Every ROOT file begins with this header, which describes the file's structure
 * and provides the byte offsets needed to locate all internal records. It is
 * never compressed and always starts with the magic bytes "root".
 *
 * ROOT files are big-endian and self-describing: the header points to a
 * [StreamerInfo] record embedded in the file itself, which defines the schema
 * of every object stored within. This allows ROOT files to be read without
 * external class definitions.
 *
 * When [isLargeFile] is true (file exceeds 2 GB), offset fields [end],
 * [seekFree], and [seekInfo] are stored as 8-byte integers on disk instead of
 * 4-byte integers, and 1,000,000 is added to [version] as a signal.
 *
 * Reference: https://root.cern/doc/v628/header.html
 *
 * @property version File format version, encoded as 10000*major + 100*minor + cycle
 *   (e.g. 62206 means ROOT 6.22.06). If >= 1,000,000 the file is a large file (>2 GB).
 *
 * @property begin Byte offset of the first data record (TKey) in the file.
 *   Fixed at 100 in all modern ROOT files — the first 100 bytes are always this header.
 *
 * @property end Byte offset of the first free (unused) byte at the end of the file.
 *   Effectively the file size. Used to know where the last record ends.
 *
 * @property seekFree Byte offset of the FreeSegments record, which tracks all
 *   unused gaps in the file available for reuse when objects are deleted or updated.
 *
 * @property nbytesFree Number of bytes occupied by the FreeSegments record.
 *
 * @property nfree Number of free segment entries in the FreeSegments record.
 *   A value of 1 typically means the file has no internal gaps (clean, sequential write).
 *
 * @property nbytesName Number of bytes used by the TKey + TNamed structure of the
 *   root TFile record (the first data record). Used to locate the root directory object.
 *
 * @property units Number of bytes used to store file pointers on disk.
 *   4 for standard files, 8 for large files (>2 GB). Mirrors [isLargeFile].
 *
 * @property compress Compression algorithm and level encoded as algorithm*100 + level.
 *   For example, 101 means zlib at level 1. Level 0 means no compression.
 *   Individual records may override this; the StreamerInfo record is always
 *   compressed at level 1 regardless of this setting.
 *
 * @property seekInfo Byte offset of the StreamerInfo record. StreamerInfo is a
 *   self-contained schema descriptor: it lists every class stored in the file along
 *   with their data member names, types, and versions. This is what makes ROOT files
 *   self-describing and enables schema evolution when class definitions change.
 *
 * @property nbytesInfo Number of bytes occupied by the StreamerInfo record.
 *
 * @property uuid 16-byte Universally Unique Identifier (UUID) for this file.
 *   Assigned at creation time. Used by TRef and TRefArray to identify cross-file
 *   object references unambiguously across time and space.
 */
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
    val uuid: ByteArray
) {
    /**
     * True if this is a large file (>2 GB), indicated by [version] >= 1,000,000.
     * In large files, the offset fields [end], [seekFree], and [seekInfo] were
     * stored as 8-byte integers on disk rather than 4-byte integers.
     */
    val isLargeFile: Boolean get() = version >= 1_000_000

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RootFileHeader
        if (version != other.version) return false
        if (begin != other.begin) return false
        if (end != other.end) return false
        if (seekFree != other.seekFree) return false
        if (nbytesFree != other.nbytesFree) return false
        if (nfree != other.nfree) return false
        if (nbytesName != other.nbytesName) return false
        if (units != other.units) return false
        if (compress != other.compress) return false
        if (seekInfo != other.seekInfo) return false
        if (nbytesInfo != other.nbytesInfo) return false
        if (!uuid.contentEquals(other.uuid)) return false
        if (isLargeFile != other.isLargeFile) return false
        return true
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + begin
        result = 31 * result + end.hashCode()
        result = 31 * result + seekFree.hashCode()
        result = 31 * result + nbytesFree
        result = 31 * result + nfree
        result = 31 * result + nbytesName
        result = 31 * result + units
        result = 31 * result + compress
        result = 31 * result + seekInfo.hashCode()
        result = 31 * result + nbytesInfo
        result = 31 * result + uuid.contentHashCode()
        result = 31 * result + isLargeFile.hashCode()
        return result
    }
}