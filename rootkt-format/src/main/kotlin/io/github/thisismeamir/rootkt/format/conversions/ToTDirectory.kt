package io.github.thisismeamir.rootkt.format.conversions

import io.github.thisismeamir.rootkt.format.models.TDirectory
import io.github.thisismeamir.rootkt.format.models.TDirectoryData
import io.github.thisismeamir.rootkt.format.models.TKey

fun Pair<TKey, TDirectoryData>.toTDirectory(): TDirectory {
    return TDirectory(
        key = this.first,
        data = this.second
    )
}