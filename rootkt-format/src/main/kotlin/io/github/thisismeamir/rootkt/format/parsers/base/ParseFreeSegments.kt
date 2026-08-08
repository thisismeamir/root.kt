package io.github.thisismeamir.rootkt.format.parsers.base

import io.github.thisismeamir.rootkt.format.models.base.FreeSegment
import io.github.thisismeamir.rootkt.format.models.base.FreeSegments
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