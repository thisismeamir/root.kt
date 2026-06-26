package io.github.thisismeamir.rootkt.format.walkers

import io.github.thisismeamir.rootkt.format.models.TObject
import java.nio.ByteBuffer


const val K_IS_REFERENCED_MASK = 0x00000010

fun ByteBuffer.readTObject(): TObject {
    val version = short
    val fUniqueID = int
    val fBits = int

    // Check if the object is referenced by a pointer to a persistent object
    val pidf = if ((fBits and K_IS_REFERENCED_MASK) != 0) {
        short // Only consume these 2 bytes if the bit flag is set
    } else {
        null
    }

    return TObject(
        version = version,
        uniqueID = fUniqueID,
        bits = fBits,
        pidf = pidf
    )
}