package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.streamer.models.STLType

fun Int.toSTLType(): STLType = when (this) {
    1 -> STLType.Vector
    2 -> STLType.List
    3 -> STLType.Deque
    4 -> STLType.Map
    5 -> STLType.Set
    6 -> STLType.MultiMap
    7 -> STLType.MultiSet
    else -> throw IllegalArgumentException("Invalid or unknown STL container flag map code: $this")
}