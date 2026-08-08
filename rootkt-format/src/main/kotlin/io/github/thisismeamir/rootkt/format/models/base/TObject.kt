package io.github.thisismeamir.rootkt.format.models.base

data class TObject(
    val version: Short,
    val uniqueID: Int,
    val bits: Int,
    val pidf: Short? = null
)
 {
    val canDelete: Boolean get() = bits and 0x00000001 != 0
    val mustCleanup: Boolean get() = bits and 0x00000008 != 0
    val isReferenced: Boolean get() = bits and 0x00000010 != 0
    val notUsable: Boolean get() = bits and 0x00002000 != 0
    val isOnHeap: Boolean get() = bits and 0x01000000 != 0
    val notDeleted: Boolean get() = bits and 0x02000000 != 0
}