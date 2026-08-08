package io.github.thisismeamir.rootkt.streamer.readers

import io.github.thisismeamir.rootkt.format.service.ClassResolver
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.parsers.classinfo.parseAndResolveClassInfo
import io.github.thisismeamir.rootkt.format.parsers.base.parseTNamed
import io.github.thisismeamir.rootkt.format.parsers.base.parseTString
import io.github.thisismeamir.rootkt.streamer.conversions.toSTLType
import io.github.thisismeamir.rootkt.streamer.conversions.toStreamerType
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerBase
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerBasicPointer
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerBasicType
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerElement
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerLoop
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerObject
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerObjectAny
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerObjectPointer
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerSTL
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerSTLString
import io.github.thisismeamir.rootkt.streamer.models.streamerelement.TStreamerString
import java.nio.ByteBuffer

fun ByteBuffer.readTStreamerElement(classResolver: ClassResolver): TStreamerElement {

    val globalPayloadStart = position()
    val globalByteCount = readByteCount()
    val className = parseAndResolveClassInfo(classResolver)

    val elementPayloadStart = position()
    val elementByteCount = readByteCount()
    val elementVersion = short
    println("elem@$globalPayloadStart class=$className byteCount=$elementByteCount globalByteCount=$globalByteCount")

    val internalByteCount = readByteCount()
    val internalVersion = short

    val tNamed = parseTNamed()
    val rawType = int
    val fSize = int
    val fArrayLength = int
    val fArrayDim = int

    val fMaxIndex = List(5) { int }
    val fTypeName = parseTString()
    val fType = rawType.toStreamerType()

//    position(elementPayloadStart + elementByteCount + 4)
    println("class Name: $className")
    val result: TStreamerElement = when (className) {
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
            val fCountName = parseTString()
            val fCountClass = parseTString()
            TStreamerBasicPointer(
                byteCount = elementByteCount, version = elementVersion, named = tNamed,
                fType = fType, fSize = fSize, fArrayLength = fArrayLength, fArrayDim = fArrayDim,
                fMaxIndex = fMaxIndex, fTypeName = fTypeName, fCountVersion = fCountVersion,
                fCountName = fCountName, fCountClass = fCountClass
            )
        }

        "TStreamerLoop" -> {
            val fCountVersion = int
            val fCountName = parseTString()
            val fCountClass = parseTString()
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
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
        )

        "TStreamerString" -> TStreamerString(
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
        )

        "TStreamerObject" -> TStreamerObject(
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
        )

        "TStreamerObjectPointer" -> TStreamerObjectPointer(
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
        )

        "TStreamerObjectAny" -> TStreamerObjectAny(
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
        )

        "TStreamerSTLString" -> TStreamerSTLString(
            elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName
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

    position(elementPayloadStart + elementByteCount + 4)
    return result
}