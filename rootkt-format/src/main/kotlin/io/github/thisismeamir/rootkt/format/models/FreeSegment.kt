package io.github.thisismeamir.rootkt.format.models

data class FreeSegment(
    val first: Long,  // first byte of free gap
    val last: Long    // last byte of free gap
) {
    val size: Long get() = last - first + 1
}