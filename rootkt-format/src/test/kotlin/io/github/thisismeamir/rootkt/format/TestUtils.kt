package io.github.thisismeamir.rootkt.format

import io.github.thisismeamir.rootkt.compression.algorithms.zlibCompress
import io.github.thisismeamir.rootkt.format.models.TKey
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.assertTrue


fun twoKeyBuffer(): ByteBuffer {
    val buf = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN)

    // Key 1: TFile, uncompressed, objLen == dataSize == 77
    buf.putInt(126); buf.putShort(4); buf.putInt(77)
    buf.putInt(0x7c6ea255); buf.putShort(49); buf.putShort(1)
    buf.putInt(100); buf.putInt(0)
    buf.put(5); buf.put("TFile".toByteArray())
    buf.put(15); buf.put("events_100.root".toByteArray())
    buf.put(0)
    buf.position(126)  // 77 zero bytes = raw payload

    // Build real compressed payload for key 2
    val rawData = "ReconstructedParticles data".repeat(20).toByteArray()
    val compressed = zlibCompress(rawData)
    // 9-byte ROOT compression header (little-endian sizes)
    val compHeader = ByteArray(9).also {
        it[0] = 'Z'.code.toByte()
        it[1] = 'L'.code.toByte()
        it[2] = 6  // level
        it[3] = (compressed.size and 0xFF).toByte()
        it[4] = (compressed.size shr 8 and 0xFF).toByte()
        it[5] = (compressed.size shr 16 and 0xFF).toByte()
        it[6] = (rawData.size and 0xFF).toByte()
        it[7] = (rawData.size shr 8 and 0xFF).toByte()
        it[8] = (rawData.size shr 16 and 0xFF).toByte()
    }
    val fullPayload = compHeader + compressed
    val keyLen2: Short = 105
    val nbytes2 = keyLen2 + fullPayload.size

    // Key 2: TBasket, large key
    val key2Start = 126
    buf.position(key2Start)
    buf.putInt(nbytes2)
    buf.putShort(1004)                   // version > 1000 = large
    buf.putInt(rawData.size)             // objLen = uncompressed size
    buf.putInt(0x7c6ea260)
    buf.putShort(keyLen2)
    buf.putShort(0)
    buf.putLong(126)                     // seekKey
    buf.putLong(100)                     // seekPdir
    buf.put(7); buf.put("TBasket".toByteArray())
    buf.put(36); buf.put("ReconstructedParticles.covMatrix[10]".toByteArray())
    buf.put(6); buf.put("events".toByteArray())

    // write actual compressed payload
    buf.position(key2Start + keyLen2)
    buf.put(fullPayload)

    buf.rewind()
    return buf
}



fun assertNameExists(name: String, keys: List<TKey>) {
    assertTrue(keys.any { it.name == name }, "Expected to find a key with name '$name', but it was not found among the keys: ${keys.map{it.name}}")
}
