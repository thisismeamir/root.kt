package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.RootFreeSegment
import io.github.thisismeamir.rootkt.format.models.RootFreeSegments
import java.nio.ByteBuffer

fun ByteBuffer.parseFreeSegments(large: Boolean): RootFreeSegments {
    val nfree = int
    val segments = (0 until nfree).map {
        val first = if (large) long else int.toLong()
        val last  = if (large) long else int.toLong()
        RootFreeSegment(first, last)
    }
    return RootFreeSegments(segments)
}