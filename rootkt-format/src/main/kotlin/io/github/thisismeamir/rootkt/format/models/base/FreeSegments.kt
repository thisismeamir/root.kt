package io.github.thisismeamir.rootkt.format.models.base


data class FreeSegments(
    val segments: List<FreeSegment>
) {
    // Last entry is virtual — represents space after fEND, not on disk
    val onDisk: List<FreeSegment> get() = segments.dropLast(1)
    val totalFreeBytes: Long get() = onDisk.sumOf { it.size }
}