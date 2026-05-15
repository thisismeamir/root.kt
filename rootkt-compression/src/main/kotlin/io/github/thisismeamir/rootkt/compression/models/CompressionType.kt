package io.github.thisismeamir.rootkt.compression.models

enum class CompressionType(val magic: String) {
    ZLIB("ZL"),
    LZMA("XZ"),
    LZ4("L4"),
    ZSTD("ZS"),
    NONE("")
}