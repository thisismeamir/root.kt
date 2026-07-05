package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import io.github.thisismeamir.rootkt.format.walkers.readTString
import io.github.thisismeamir.rootkt.streamer.models.*
import java.nio.ByteBuffer

fun ByteBuffer.readTStreamerElement(): TStreamerElement {

    val globalPayloadStart = position()
    val globalByteCount = readByteCount()
    val classInfo = readClassInfo()

    val elementPayloadStart = position()
    val elementByteCount = readByteCount()
    val elementVersion = short

    val internalByteCount = readByteCount()
    val internalVersion = short

    val tNamed = readTNamed()
    // Core Streamer Metadata
    val rawType = int
    val fSize = int
    val fArrayLength = int
    val fArrayDim = int

    // Map fMaxIndex to List<Int> instead of IntArray
    val fMaxIndex = List(5) { int }
    val fTypeName = readTString()
    // Safely parse out the mapped StreamerType layout
    val fType = rawType.toStreamerType()

    // Ensure accurate structural cursor alignment before branching into subclasses
    position(elementPayloadStart + elementByteCount + 4)
    println("class Name: ${classInfo.className}")
    val result: TStreamerElement = when (classInfo.className) {
        "TStreamerBase" -> {
            val fBaseVersion = int
            TStreamerBase(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName, fBaseVersion = fBaseVersion
            )
        }

        "TStreamerBasicPointer" -> {
            val fCountVersion = int
            val fCountName = readTString()
            val fCountClass = readTString()
            TStreamerBasicPointer(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName, fCountVersion = fCountVersion,
                fCountName = fCountName, fCountClass = fCountClass
            )
        }

        "TStreamerLoop" -> {
            val fCountVersion = int
            val fCountName = readTString()
            val fCountClass = readTString()
            TStreamerLoop(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName, fCountVersion = fCountVersion,
                fCountName = fCountName, fCountClass = fCountClass
            )
        }

        "TStreamerSTL" -> {
            val rawSTLType = int
            val rawCType = int
            TStreamerSTL(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName,
                fSTLType = rawSTLType.toSTLType(),
                fCType = rawCType.toStreamerType()
            )
        }

        "TStreamerBasicType" -> TStreamerBasicType(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        "TStreamerString" -> TStreamerString(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        "TStreamerObject" -> TStreamerObject(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        "TStreamerObjectPointer" -> TStreamerObjectPointer(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        "TStreamerObjectAny" -> TStreamerObjectAny(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        "TStreamerSTLString" -> TStreamerSTLString(
            elementByteCount,
            elementVersion,
            tNamed,
            fType,
            fSize,
            fArrayLength,
            fArrayDim,
            fMaxIndex,
            fTypeName
        )

        else -> {

            val fBaseVersion = int
            TStreamerBase(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName, fBaseVersion = fBaseVersion
            )
        }
    }

    // Resolve stack layouts back out cleanly to global alignments
    position(globalPayloadStart + globalByteCount + 4)

    return result
}