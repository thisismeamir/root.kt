package io.github.thisismeamir.rootkt.compression.algorithms

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

fun zlibCompress(data: ByteArray, level: Int = 6): ByteArray {
    val deflater = Deflater(level)
    val output = ByteArrayOutputStream()
    try {
        deflater.setInput(data)
        deflater.finish()
        val buf = ByteArray(8192)
        while (!deflater.finished())
            output.write(buf, 0, deflater.deflate(buf))
        return output.toByteArray()
    } finally {
        deflater.end()
    }
}

fun zlibDecompress(compressed: ByteArray, uncompressedSize: Int): ByteArray {
    val inflater = Inflater()
    val output = ByteArray(uncompressedSize)
    try {
        inflater.setInput(compressed)
        val n = inflater.inflate(output)
        check(n == uncompressedSize) { "zlib: expected $uncompressedSize bytes, got $n" }
        return output
    } finally {
        inflater.end()
    }
}