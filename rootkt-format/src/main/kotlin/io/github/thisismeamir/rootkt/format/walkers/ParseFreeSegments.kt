package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.FreeSegment
import io.github.thisismeamir.rootkt.format.models.FreeSegments
import java.nio.ByteBuffer

fun ByteBuffer.parseFreeSegments(large: Boolean): FreeSegments {
    val nfree = int
    val segments = (0 until nfree).map {
        val first = if (large) long else int.toLong()
        val last  = if (large) long else int.toLong()
        FreeSegment(first, last)
    }
    return FreeSegments(segments)
}