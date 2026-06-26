package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.format.models.RawTObjectArray
import io.github.thisismeamir.rootkt.format.models.TObject
import io.github.thisismeamir.rootkt.format.models.TObjectArray
import io.github.thisismeamir.rootkt.format.utils.readByteCount
import io.github.thisismeamir.rootkt.format.walkers.readClassInfo
import io.github.thisismeamir.rootkt.format.walkers.readTNamed
import io.github.thisismeamir.rootkt.format.walkers.readTString
import io.github.thisismeamir.rootkt.streamer.STLType
import io.github.thisismeamir.rootkt.streamer.StreamerType
import io.github.thisismeamir.rootkt.streamer.TStreamerBase
import io.github.thisismeamir.rootkt.streamer.TStreamerBasicPointer
import io.github.thisismeamir.rootkt.streamer.TStreamerBasicType
import io.github.thisismeamir.rootkt.streamer.TStreamerElement
import io.github.thisismeamir.rootkt.streamer.TStreamerLoop
import io.github.thisismeamir.rootkt.streamer.TStreamerObject
import io.github.thisismeamir.rootkt.streamer.TStreamerObjectAny
import io.github.thisismeamir.rootkt.streamer.TStreamerObjectPointer
import io.github.thisismeamir.rootkt.streamer.TStreamerSTL
import io.github.thisismeamir.rootkt.streamer.TStreamerSTLString
import io.github.thisismeamir.rootkt.streamer.TStreamerString
import java.nio.ByteBuffer
import java.nio.ByteOrder


fun RawTObjectArray.readAsTStreamerElement(): TObjectArray<TStreamerElement> {
    if (numberOfObjects <= 0) return TObjectArray(
        classInfo = this.classInfo,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        fLowerBound = this.fLowerBound,
        objects = emptyList()
    )

    // Wrap the raw payload segment into a dedicated readable cursor view
    val buffer = ByteBuffer.wrap(this.objects).order(ByteOrder.BIG_ENDIAN)
    val elementsList = ArrayList<TStreamerElement>(numberOfObjects)
    println(" reached and found $numberOfObjects")
    for (i in 0 until (numberOfObjects - 1)) {
        // Sequentially parse each typed polymorphic structure
        val element = buffer.readTStreamerElement()
        elementsList.add(element)
    }

    return TObjectArray(
        classInfo = this.classInfo,
        version = this.version,
        tObject = this.tObject,
        fName = this.fName,
        numberOfObjects = this.numberOfObjects,
        fLowerBound = this.fLowerBound,
        objects = elementsList
    )
}


fun ByteBuffer.readTStreamerElement(): TStreamerElement {
    // Layer 1 Envelopes
    val globalByteCount = readByteCount()
    val globalPayloadStart = position()

    val classInfo = readClassInfo()
    val innerByteCount = readByteCount()
    val innerPayloadStart = position()

    val streamerClassVersion = short

    // Layer 2 Envelope
    val elementByteCount = readByteCount()
    val elementPayloadStart = position()
    val elementVersion = short

    // Layer 3 Base Object
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
    position(elementPayloadStart + elementByteCount)

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
        "TStreamerBasicType"     -> TStreamerBasicType(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)
        "TStreamerString"        -> TStreamerString(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)
        "TStreamerObject"        -> TStreamerObject(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)
        "TStreamerObjectPointer" -> TStreamerObjectPointer(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)
        "TStreamerObjectAny"     -> TStreamerObjectAny(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)
        "TStreamerSTLString"     -> TStreamerSTLString(elementByteCount, elementVersion, tNamed, fType, fSize, fArrayLength, fArrayDim, fMaxIndex, fTypeName)

        else -> throw IllegalArgumentException("Unknown polymorphic streamer structural class name: ${classInfo.className}")
    }

    // Resolve stack layouts back out cleanly to global alignments
    position(innerPayloadStart + innerByteCount)
    position(globalPayloadStart + globalByteCount)

    return result
}

private fun Int.toStreamerType(): StreamerType {
    val typeCode = this
    return when {
        typeCode == 0  -> StreamerType.Object.Base
        typeCode == 6  -> StreamerType.Counter
        typeCode == 15 -> StreamerType.BitMask
        typeCode == 65 -> StreamerType.Object.TString
        typeCode == 66 -> StreamerType.Object.TObject
        typeCode == 67 -> StreamerType.Object.TNamed
        typeCode == 61 -> StreamerType.Object.Derived
        typeCode == 62 -> StreamerType.Object.Any
        typeCode == 63 -> StreamerType.ObjectPointer.NonNull
        typeCode == 64 -> StreamerType.ObjectPointer.Nullable
        typeCode == 500 -> StreamerType.STL
        typeCode == 501 -> StreamerType.ObjectArray

        // Pointers to core types (40 + type)
        typeCode in 41..54 -> {
            val baseBuiltIn = (typeCode - 40).toStreamerType() as StreamerType.BuiltIn
            StreamerType.Pointer(of = baseBuiltIn)
        }

        // Arrays of core types (20 + type)
        typeCode in 21..34 -> {
            val baseBuiltIn = (typeCode - 20).toStreamerType() as StreamerType.BuiltIn
            StreamerType.Array(of = baseBuiltIn)
        }

        // Basic scalar primitives
        else -> when (typeCode) {
            1  -> StreamerType.BuiltIn.Char
            2  -> StreamerType.BuiltIn.Short
            3  -> StreamerType.BuiltIn.Int
            4  -> StreamerType.BuiltIn.Long
            5  -> StreamerType.BuiltIn.Float
            8  -> StreamerType.BuiltIn.Double
            11 -> StreamerType.BuiltIn.UChar
            12 -> StreamerType.BuiltIn.UShort
            13 -> StreamerType.BuiltIn.UInt
            14 -> StreamerType.BuiltIn.ULong
            365 -> StreamerType.Object.TString // Special fallback map for container inner strings
            else -> throw IllegalArgumentException("Unrecognized binary ROOT data type code definition: $typeCode")
        }
    }
}

private fun Int.toSTLType(): STLType = when (this) {
    1 -> STLType.Vector
    2 -> STLType.List
    3 -> STLType.Deque
    4 -> STLType.Map
    5 -> STLType.Set
    6 -> STLType.MultiMap
    7 -> STLType.MultiSet
    else -> throw IllegalArgumentException("Invalid or unknown STL container flag map code: $this")
}