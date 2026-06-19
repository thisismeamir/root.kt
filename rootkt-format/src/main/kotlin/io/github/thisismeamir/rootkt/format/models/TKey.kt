package io.github.thisismeamir.rootkt.format.models

/**
 * Represents the key (index entry) of a single data record in a ROOT file.
 *
 * A ROOT file is a flat sequence of data records, each preceded by a TKey.
 * The TKey is never compressed and acts as both a directory entry and a
 * record header: it tells you what the object is, where it lives on disk,
 * how large it is, and whether its data payload is compressed.
 *
 * To read an object from a ROOT file you:
 *   1. Locate its TKey (via the KeysList record or by walking from [seekKey])
 *   2. Seek to [seekKey] + [keyLen] to reach the raw data payload
 *   3. Read [dataSize] bytes
 *   4. If [isCompressed], decompress to recover [objLen] bytes
 *   5. Pass the uncompressed buffer to the appropriate streamer
 *
 * Keys are also used internally by ROOT for its own bookkeeping records:
 * TFile, TDirectory, KeysList, FreeSegments, and StreamerInfo all have TKeys.
 * These internal keys never appear in the user-visible KeysList.
 *
 * When a key is located past the 32-bit file limit (>2 GB), [seekKey] and
 * [seekPdir] are stored as 8-byte integers on disk. This is signalled by
 * [version] > 1000 (i.e. [isLarge] == true), independently of whether the
 * file header itself is a large file.
 *
 * Reference: https://root.cern/doc/v628/datarecord.html
 *
 * @property nbytes Total number of bytes for this record on disk, including
 *   both the key structure and the (possibly compressed) data payload.
 *   The next TKey in the file starts at this key's offset + [nbytes].
 *   A negative value here indicates a free (deleted) segment, not a valid key.
 *
 * @property version TKey format version identifier. Values > 1000 indicate a
 *   large key where [seekKey] and [seekPdir] occupy 8 bytes each on disk instead
 *   of 4 bytes. Unrelated to the ROOT release version.
 *
 * @property objLen Number of bytes of the object's data in its uncompressed form.
 *   If equal to [dataSize], the payload is not compressed. If larger, the payload
 *   on disk is compressed and must be decompressed before deserializing the object.
 *
 * @property datime Date and time when this record was written to the file, packed
 *   into a single Int as: (year-1995)<<26 | month<<22 | day<<17 | hour<<12 | min<<6 | sec.
 *
 * @property keyLen Number of bytes occupied by this key structure on disk.
 *   The data payload begins at [seekKey] + [keyLen].
 *   Varies per key because [className], [name], and [title] are variable-length.
 *
 * @property cycle Cycle number of this key. ROOT allows multiple versions of an
 *   object with the same name to coexist in a directory, distinguished by their
 *   cycle number. The highest cycle is the most recent version. Together,
 *   [name] and [cycle] uniquely identify a record within its directory.
 *
 * @property seekKey Absolute byte offset of this record in the file (points to the
 *   start of this TKey itself). Used as a consistency check: if you are reading
 *   this key at offset X, [seekKey] should equal X.
 *
 * @property seekPdir Absolute byte offset of the parent directory's TKey.
 *   For the root TFile record this is 0 (no parent). For all other records it
 *   points to the TFile or TDirectory key that contains this object.
 *   Note: the 16 highest bits of this field on disk encode a pid offset used
 *   by TRef for cross-file object references.
 *
 * @property className Name of the ROOT class of the stored object, e.g. "TH1F",
 *   "TTree", "TBasket", "TFile". Used to look up the correct streamer in the
 *   StreamerInfo registry when deserializing the data payload.
 *
 * @property name Name of the stored object within its parent directory.
 *   Together with [cycle], uniquely identifies the object in its directory.
 *   Should be a valid C++ identifier for interoperability with ROOT's interpreter.
 *
 * @property title Human-readable description of the object. Optional — may be empty.
 */
data class TKey(
    val nbytes: Int,
    val version: Short,
    val objLen: Int,
    val datime: Int,
    val keyLen: Short,
    val cycle: Short,
    val seekKey: Long,
    val seekPdir: Long,
    val className: String,
    val name: String,
    val title: String
) {
    /**
     * True if this key uses 8-byte offsets for [seekKey] and [seekPdir] on disk.
     * This occurs when the key is located past the 32-bit file boundary (>2 GB),
     * signalled by [version] > 1000.
     */
    val isLarge: Boolean get() = version > 1000

    /**
     * Number of bytes of the data payload on disk (compressed or not).
     * Computed as [nbytes] - [keyLen]: the total record size minus the key structure.
     */
    val dataSize: Int get() = nbytes - keyLen

    /**
     * True if the data payload is zlib/lz4/zstd compressed on disk.
     * When true, the payload must be decompressed before deserialization.
     * The compression algorithm is identified by the first 2 bytes of the payload.
     */
    val isCompressed: Boolean get() = objLen != dataSize
}