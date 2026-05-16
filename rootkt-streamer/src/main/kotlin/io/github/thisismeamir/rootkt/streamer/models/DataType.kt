package io.github.thisismeamir.rootkt.streamer.models

enum class DataType(val code: Int) {
    NO_TYPE(0),
    CHAR(1), SHORT(2), INT(3), LONG(4), FLOAT(5),
    COUNTER(6), CHAR_STAR(7), DOUBLE(8), DOUBLE32(9),
    UCHAR(11), USHORT(12), UINT(13), ULONG(14),
    BITS(15), LONG64(16), ULONG64(17), BOOL(18), FLOAT16(19),

    // arrays: base + 20
    // pointers: base + 40
    OBJECT(61), ANY(62), OBJECTP(63), ANYP(64),
    TSTRING(65), TOBJECT(66), TNAMED(67),
    STL(300), STLSTRING(365),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Int) = entries.find { it.code == code } ?: UNKNOWN
    }
}