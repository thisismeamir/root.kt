package io.github.thisismeamir.rootkt.format.models.directory

import io.github.thisismeamir.rootkt.format.models.base.FileHeader
import io.github.thisismeamir.rootkt.format.models.base.TKey

data class TDirectoryRoot(
    val header: FileHeader,
    val data: TDirectoryData,
    val objectKeys: List<TKey>,
    val children: List<TDirectoryNode>
)