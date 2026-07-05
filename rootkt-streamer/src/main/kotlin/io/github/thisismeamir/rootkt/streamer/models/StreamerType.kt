package io.github.thisismeamir.rootkt.streamer.models

sealed class StreamerType {
    sealed class BuiltIn : StreamerType() {
        object Char : BuiltIn()
        object Short : BuiltIn()
        object Int : BuiltIn()
        object Long : BuiltIn()
        object Float : BuiltIn()
        object Double : BuiltIn()
        object Double32 : BuiltIn()
        object UChar : BuiltIn()
        object UShort : BuiltIn()
        object UInt : BuiltIn()
        object ULong : BuiltIn()
        object Long64 : BuiltIn()
        object ULong64 : BuiltIn()
        object Bool : BuiltIn()
        object Float16 : BuiltIn()
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
        object AnyNonNull : ObjectPointer()
        object AnyNullable : ObjectPointer()
        object AnyNoVT : ObjectPointer()
    }
    object Counter : StreamerType()
    object BitMask : StreamerType()
    object ObjectArray : StreamerType()
    object STL : StreamerType()
    object STLPointer : StreamerType()
}