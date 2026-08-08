package io.github.thisismeamir.rootkt.format.conversions

import io.github.thisismeamir.rootkt.format.models.directory.TDirectory
import io.github.thisismeamir.rootkt.format.models.directory.TDirectoryData
import io.github.thisismeamir.rootkt.format.models.base.TKey

fun Pair<TKey, TDirectoryData>.toTDirectory(): TDirectory {
    return TDirectory(
        key = this.first,
        data = this.second
    )
}