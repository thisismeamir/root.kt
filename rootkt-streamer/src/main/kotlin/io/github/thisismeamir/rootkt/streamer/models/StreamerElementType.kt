package io.github.thisismeamir.rootkt.streamer.models

enum class StreamerElementType {
    BASE, BASIC_TYPE, BASIC_POINTER,
    LOOP, OBJECT, OBJECT_ANY,
    OBJECT_POINTER, OBJECT_ANY_POINTER,
    STRING, STL, STL_STRING,
    UNKNOWN
}