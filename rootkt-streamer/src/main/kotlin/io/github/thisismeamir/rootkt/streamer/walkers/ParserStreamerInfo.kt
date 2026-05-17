package io.github.thisismeamir.rootkt.streamer.walkers

import io.github.thisismeamir.rootkt.streamer.models.DataType
import io.github.thisismeamir.rootkt.streamer.models.RootStreamerElement
import io.github.thisismeamir.rootkt.streamer.models.RootStreamerInfo
import io.github.thisismeamir.rootkt.streamer.models.RootStreamerList
import java.nio.ByteBuffer
import kotlin.collections.get
import kotlin.text.get

private const val kByteCountMask = 0x40000000
private const val kNewClassTag   = 0xFFFFFFFF.toInt()
private const val kClassMask     = 0x80000000.toInt()

fun ByteBuffer.parseStreamerList(): RootStreamerList {
    // class tag map: offset → className, for back-references
    val classMap = mutableMapOf<Int, String>()
    val startPos = position()

    // TList header
    val listByteCount = int and kByteCountMask.inv()  // strip mask
    val listVersion   = short
    // TObject base (fUniqueID + fBits = 8 bytes, but ROOT writes 2+4+4 = 10 bytes with version)
    skipTObject()
    val listNameLen = get().toInt() and 0xFF
    if (listNameLen > 0) skip(listNameLen)   // unnamed in StreamerInfo record
    val nObjects = int

    val infos = (0 until nObjects).map {
        parseTStreamerInfo(classMap, startPos)
    }
    return RootStreamerList(infos)
}

fun ByteBuffer.parseTStreamerInfo(
    classMap: MutableMap<Int, String>,
    recordStart: Int
): RootStreamerInfo {
    val objStart = position()

    // ByteCount of TStreamerInfo object
    val byteCount = int
    val remaining = byteCount and kByteCountMask.inv()

    // ClassInfo — new class or back-reference
    val classTagPos = position()
    val classTag = int
    val className = when {
        classTag == kNewClassTag -> {
            val name = readNullTerminatedString()
            classMap[classTagPos - recordStart] = name
            name
        }
        (classTag and kClassMask) != 0 -> {
            val refOffset = classTag and kClassMask.inv()
            classMap[refOffset] ?: error("Unknown class back-reference at $refOffset")
        }
        else -> error("Unexpected class tag: $classTag")
    }

    // Version of TStreamerInfo class itself
    val siVersion = short

    // TNamed base
    val tnamedByteCount = int  // ByteCount | mask
    val tnamedVersion   = short
    skipTObject()
    val siClassName = readTString()   // the class this StreamerInfo describes
    val siTitle     = readTString()   // usually empty

    // TStreamerInfo own fields
    val checksum     = int.toLong() and 0xFFFFFFFFL
    val classVersion = int

    // TObjArray of elements
    val elements = parseTObjArray(classMap, recordStart)

    return RootStreamerInfo(siClassName, checksum, classVersion, elements)
}

fun ByteBuffer.parseTObjArray(
    classMap: MutableMap<Int, String>,
    recordStart: Int
): List<RootStreamerElement> {
    val byteCount   = int
    val classTag    = int
    val className   = resolveClassTag(classTag, classMap, recordStart)
    val version     = short
    skipTObject()
    val name        = readTString()
    val nObjects    = int
    val low         = int  // fLowerBound, unused

    return (0 until nObjects).map {
        parseTStreamerElement(classMap, recordStart)
    }
}

fun ByteBuffer.parseTStreamerElement(
    classMap: MutableMap<Int, String>,
    recordStart: Int
): RootStreamerElement {
    val byteCount = int
    val objEnd    = position() + (byteCount and kByteCountMask.inv())

    val classTag  = int
    val elemClass = resolveClassTag(classTag, classMap, recordStart)
    val version   = short

    // TNamed base
    val tnamedByteCount = int
    val tnamedVersion   = short
    skipTObject()
    val name  = readTString()
    val title = readTString()

    // TStreamerElement base fields
    val offset      = int
    val type        = DataType.fromCode(int)
    val size        = int
    val arrayLength = int
    val arrayDim    = int
    val maxIndex    = IntArray(5) { int }
    val typeName    = readTString()

    // Extra fields per subtype
    val element = when (elemClass) {
        "TStreamerBase" -> {
            val baseVersion = if (version >= 2) int else 0
            RootStreamerElement.Base(name, title, offset, type, size,
                arrayLength, arrayDim, maxIndex, typeName, baseVersion)
        }
        "TStreamerBasicPointer" -> {
            val countVersion = int
            val countName    = readTString()
            val countClass   = readTString()
            RootStreamerElement.BasicPointer(name, title, offset, type, size,
                arrayLength, arrayDim, maxIndex, typeName, countVersion, countName, countClass)
        }
        "TStreamerLoop" -> {
            val countVersion = int
            val countName    = readTString()
            val countClass   = readTString()
            RootStreamerElement.Loop(name, title, offset, type, size,
                arrayLength, arrayDim, maxIndex, typeName, countVersion, countName, countClass)
        }
        "TStreamerSTL", "TStreamerSTLstring" -> {
            val stlType = int
            val cType   = int
            if (elemClass == "TStreamerSTLstring")
                RootStreamerElement.StlString(name, title, offset, type, size,
                    arrayLength, arrayDim, maxIndex, typeName, stlType, cType)
            else
                RootStreamerElement.Stl(name, title, offset, type, size,
                    arrayLength, arrayDim, maxIndex, typeName, stlType, cType)
        }
        "TStreamerObject"           ->
            RootStreamerElement.Object(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        "TStreamerObjectAny"        ->
            RootStreamerElement.ObjectAny(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        "TStreamerObjectPointer"    ->
            RootStreamerElement.ObjectPointer(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        "TStreamerObjectAnyPointer" ->
            RootStreamerElement.ObjectAnyPointer(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        "TStreamerString"           ->
            RootStreamerElement.RootString(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        "TStreamerBasicType"        ->
            RootStreamerElement.BasicType(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName)
        else ->
            RootStreamerElement.Unknown(name, title, offset, type, size, arrayLength, arrayDim, maxIndex, typeName, elemClass)
    }

    // seek to end of element using ByteCount — handles unknown/future fields
    position(objEnd)
    return element
}

// --- helpers ---

fun ByteBuffer.resolveClassTag(
    classTag: Int,
    classMap: MutableMap<Int, String>,
    recordStart: Int
): String {
    val tagPos = position() - 4
    return when {
        classTag == kNewClassTag -> {
            val name = readNullTerminatedString()
            classMap[tagPos - recordStart] = name
            name
        }
        (classTag and kClassMask) != 0 -> {
            val refOffset = classTag and kClassMask.inv()
            classMap[refOffset] ?: error("Unknown class back-ref: $refOffset, map: $classMap")
        }
        else -> error("Unexpected class tag: 0x${classTag.toString(16)}")
    }
}

fun ByteBuffer.skipTObject() {
    // TObject = fUniqueID (UInt 4) + fBits (UInt 4) = 8 bytes
    // But written with a 2-byte version prefix = 10 bytes total
    skip(10)
}

fun ByteBuffer.readTString(): String {
    val len = get().toInt() and 0xFF
    if (len == 0) return ""
    // ROOT uses 255 as a sentinel for strings longer than 254 bytes
    val realLen = if (len == 255) int else len
    return ByteArray(realLen).also { get(it) }.toString(Charsets.UTF_8)
}

fun ByteBuffer.readNullTerminatedString(): String {
    val bytes = mutableListOf<Byte>()
    while (true) {
        val b = get()
        if (b == 0.toByte()) break
        bytes.add(b)
    }
    return bytes.toByteArray().toString(Charsets.UTF_8)
}

fun ByteBuffer.skip(n: Int) { position(position() + n) }