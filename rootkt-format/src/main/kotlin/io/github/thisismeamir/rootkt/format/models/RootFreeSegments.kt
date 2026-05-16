package io.github.thisismeamir.rootkt.format.models


data class RootFreeSegments(
    val segments: List<RootFreeSegment>
) {
    // Last entry is virtual — represents space after fEND, not on disk
    val onDisk: List<RootFreeSegment> get() = segments.dropLast(1)
    val totalFreeBytes: Long get() = onDisk.sumOf { it.size }
}