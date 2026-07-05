package io.github.thisismeamir.rootkt.streamer

import io.github.thisismeamir.rootkt.streamer.models.StreamerType
import io.github.thisismeamir.rootkt.streamer.walkers.toStreamerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToStreamerTypeTest {

    @Test
    fun `maps base and structural codes`() {
        assertEquals(StreamerType.Object.Base, 0.toStreamerType())
        assertEquals(StreamerType.Counter, 6.toStreamerType())
        assertEquals(StreamerType.BitMask, 15.toStreamerType())
    }

    @Test
    fun `maps built-in scalar codes`() {
        assertEquals(StreamerType.BuiltIn.Char, 1.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Short, 2.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Int, 3.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Long, 4.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Float, 5.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Double, 8.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Double32, 9.toStreamerType())
        assertEquals(StreamerType.BuiltIn.UChar, 11.toStreamerType())
        assertEquals(StreamerType.BuiltIn.UShort, 12.toStreamerType())
        assertEquals(StreamerType.BuiltIn.UInt, 13.toStreamerType())
        assertEquals(StreamerType.BuiltIn.ULong, 14.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Long64, 16.toStreamerType())
        assertEquals(StreamerType.BuiltIn.ULong64, 17.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Bool, 18.toStreamerType())
        assertEquals(StreamerType.BuiltIn.Float16, 19.toStreamerType())
    }

    @Test
    fun `maps legacy char to Char`() {
        assertEquals(StreamerType.BuiltIn.Char, 10.toStreamerType())
    }

    @Test
    fun `maps object codes`() {
        assertEquals(StreamerType.Object.TString, 65.toStreamerType())
        assertEquals(StreamerType.Object.TObject, 66.toStreamerType())
        assertEquals(StreamerType.Object.TNamed, 67.toStreamerType())
        assertEquals(StreamerType.Object.Derived, 61.toStreamerType())
        assertEquals(StreamerType.Object.Any, 62.toStreamerType())
    }

    @Test
    fun `maps object pointer codes`() {
        assertEquals(StreamerType.ObjectPointer.NonNull, 63.toStreamerType())
        assertEquals(StreamerType.ObjectPointer.Nullable, 64.toStreamerType())
        assertEquals(StreamerType.ObjectPointer.AnyNonNull, 68.toStreamerType())
        assertEquals(StreamerType.ObjectPointer.AnyNullable, 69.toStreamerType())
        assertEquals(StreamerType.ObjectPointer.AnyNoVT, 70.toStreamerType())
    }

    @Test
    fun `maps STL codes`() {
        assertEquals(StreamerType.STL, 300.toStreamerType())
        assertEquals(StreamerType.STLPointer, 71.toStreamerType())
        assertEquals(StreamerType.Object.TString, 365.toStreamerType())
        assertEquals(StreamerType.STL, 500.toStreamerType())
        assertEquals(StreamerType.ObjectArray, 501.toStreamerType())
    }

    @Test
    fun `maps array of built-in codes via kOffsetL`() {
        assertEquals(StreamerType.Array(StreamerType.BuiltIn.Int), 23.toStreamerType())
        assertEquals(StreamerType.Array(StreamerType.BuiltIn.Bool), 38.toStreamerType())
        assertEquals(StreamerType.Array(StreamerType.BuiltIn.Float16), 39.toStreamerType())
    }

    @Test
    fun `maps pointer to built-in codes via kOffsetP`() {
        assertEquals(StreamerType.Pointer(StreamerType.BuiltIn.Int), 43.toStreamerType())
        assertEquals(StreamerType.Pointer(StreamerType.BuiltIn.Bool), 58.toStreamerType())
        assertEquals(StreamerType.Pointer(StreamerType.BuiltIn.Float16), 59.toStreamerType())
    }

    @Test
    fun `throws on unrecognized code`() {
        assertFailsWith<IllegalArgumentException> { 9999.toStreamerType() }
    }
}