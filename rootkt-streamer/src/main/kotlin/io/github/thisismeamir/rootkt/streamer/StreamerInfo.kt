package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.format.models.ClassInfo
import io.github.thisismeamir.rootkt.format.models.TNamed
import io.github.thisismeamir.rootkt.format.models.TObjectArray


data class TStreamerInfo(
    val classInfo: ClassInfo,
    val version: Short,
    val tNamed: TNamed,
    val fCheckSum: Int,
    val fClassVersion: Int,
    val objectArray: TObjectArray<TStreamerElement>
)

sealed class StreamerType {
    sealed class BuiltIn : StreamerType() {
        object Char : BuiltIn()
        object Short : BuiltIn()
        object Int : BuiltIn()
        object Long : BuiltIn()
        object Float : BuiltIn()
        object Double : BuiltIn()
        object UChar : BuiltIn()
        object UShort : BuiltIn()
        object UInt : BuiltIn()
        object ULong : BuiltIn()
    }

    data class Array(val of: BuiltIn) : StreamerType()
    data class Pointer(val of: BuiltIn) : StreamerType()

    sealed class Object : StreamerType() {
        object Base : Object()
        object TString : Object()
        object TObject : Object()
        object TNamed : Object()
        object Derived : Object()
        object Any : Object()
    }

    sealed class ObjectPointer : StreamerType() {
        object NonNull : ObjectPointer()
        object Nullable : ObjectPointer()
    }

    object Counter : StreamerType()
    object BitMask : StreamerType()
    object ObjectArray : StreamerType()
    object STL : StreamerType()
}


sealed interface TStreamerElement{
    val byteCount: Int
    val version: Short
    val named: TNamed
    val fType: StreamerType
    val fSize: Int
    val fArrayLength: Int
    val fArrayDim: Int
    val fMaxIndex: List<Int>
    val fTypeName: String
}
data class TStreamerBasicType(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerString(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerObject(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerObjectPointer(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerObjectAny(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerSTLString(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String
) : TStreamerElement

data class TStreamerBase(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String,
    val fBaseVersion: Int
) : TStreamerElement

data class TStreamerBasicPointer(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String,
    val fCountVersion: Int,
    val fCountName: String,
    val fCountClass: String
) : TStreamerElement

data class TStreamerLoop(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String,
    val fCountVersion: Int,
    val fCountName: String,
    val fCountClass: String
) : TStreamerElement

data class TStreamerSTL(
    override val byteCount: Int,
    override val version: Short,
    override val named: TNamed,
    override val fType: StreamerType,
    override val fSize: Int,
    override val fArrayLength: Int,
    override val fArrayDim: Int,
    override val fMaxIndex: List<Int>,
    override val fTypeName: String,
    val fSTLType: STLType,
    val fCType: StreamerType
) : TStreamerElement

enum class STLType {
    Vector, List, Deque, Map, Set, MultiMap, MultiSet
}