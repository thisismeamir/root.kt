package io.github.thisismeamir.rootkt.streamer.models

sealed class RootStreamerElement {
    abstract val name: String
    abstract val title: String
    abstract val offset: Int
    abstract val type: DataType
    abstract val size: Int
    abstract val arrayLength: Int
    abstract val arrayDim: Int
    abstract val maxIndex: IntArray
    abstract val typeName: String
    abstract val elementType: StreamerElementType

    data class Base(
        override val name: String,
        override val title: String,
        override val offset: Int,
        override val type: DataType,
        override val size: Int,
        override val arrayLength: Int,
        override val arrayDim: Int,
        override val maxIndex: IntArray,
        override val typeName: String,
        val baseVersion: Int
    ) : RootStreamerElement() {
        override val elementType = StreamerElementType.BASE
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Base

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (baseVersion != other.baseVersion) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + baseVersion
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class BasicType(
        override val name: String,
        override val title: String,
        override val offset: Int,
        override val type: DataType,
        override val size: Int,
        override val arrayLength: Int,
        override val arrayDim: Int,
        override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() {
        override val elementType = StreamerElementType.BASIC_TYPE
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BasicType

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class BasicPointer(
        override val name: String,
        override val title: String,
        override val offset: Int,
        override val type: DataType,
        override val size: Int,
        override val arrayLength: Int,
        override val arrayDim: Int,
        override val maxIndex: IntArray,
        override val typeName: String,
        val countVersion: Int,
        val countName: String,
        val countClass: String
    ) : RootStreamerElement() {
        override val elementType = StreamerElementType.BASIC_POINTER
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BasicPointer

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (countVersion != other.countVersion) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (countName != other.countName) return false
            if (countClass != other.countClass) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + countVersion
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + countName.hashCode()
            result = 31 * result + countClass.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class Loop(
        override val name: String,
        override val title: String,
        override val offset: Int,
        override val type: DataType,
        override val size: Int,
        override val arrayLength: Int,
        override val arrayDim: Int,
        override val maxIndex: IntArray,
        override val typeName: String,
        val countVersion: Int,
        val countName: String,
        val countClass: String
    ) : RootStreamerElement() {
        override val elementType = StreamerElementType.LOOP
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Loop

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (countVersion != other.countVersion) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (countName != other.countName) return false
            if (countClass != other.countClass) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + countVersion
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + countName.hashCode()
            result = 31 * result + countClass.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    // Object, ObjectAny, ObjectPointer, ObjectAnyPointer, String — no extra fields
    data class Object(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.OBJECT
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Object

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class ObjectAny(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.OBJECT_ANY
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ObjectAny

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class ObjectPointer(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.OBJECT_POINTER
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ObjectPointer

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class ObjectAnyPointer(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.OBJECT_ANY_POINTER
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ObjectAnyPointer

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class RootString(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.STRING
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RootString

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class Stl(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String,
        val stlType: Int,
        val cType: Int
    ) : RootStreamerElement() { override val elementType = StreamerElementType.STL
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Stl

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (stlType != other.stlType) return false
            if (cType != other.cType) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + stlType
            result = 31 * result + cType
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class StlString(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String,
        val stlType: Int,
        val cType: Int
    ) : RootStreamerElement() { override val elementType = StreamerElementType.STL_STRING
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StlString

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (stlType != other.stlType) return false
            if (cType != other.cType) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + stlType
            result = 31 * result + cType
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }

    data class Unknown(
        override val name: String, override val title: String,
        override val offset: Int, override val type: DataType,
        override val size: Int, override val arrayLength: Int,
        override val arrayDim: Int, override val maxIndex: IntArray,
        override val typeName: String,
        val rawClassName: String
    ) : RootStreamerElement() { override val elementType = StreamerElementType.UNKNOWN
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Unknown

            if (offset != other.offset) return false
            if (size != other.size) return false
            if (arrayLength != other.arrayLength) return false
            if (arrayDim != other.arrayDim) return false
            if (name != other.name) return false
            if (title != other.title) return false
            if (type != other.type) return false
            if (!maxIndex.contentEquals(other.maxIndex)) return false
            if (typeName != other.typeName) return false
            if (rawClassName != other.rawClassName) return false
            if (elementType != other.elementType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = offset
            result = 31 * result + size
            result = 31 * result + arrayLength
            result = 31 * result + arrayDim
            result = 31 * result + name.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + maxIndex.contentHashCode()
            result = 31 * result + typeName.hashCode()
            result = 31 * result + rawClassName.hashCode()
            result = 31 * result + elementType.hashCode()
            return result
        }
    }
}