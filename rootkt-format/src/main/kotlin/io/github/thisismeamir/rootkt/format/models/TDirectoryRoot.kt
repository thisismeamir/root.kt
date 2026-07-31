package io.github.thisismeamir.rootkt.format.models

data class TDirectoryRoot(
    val header: FileHeader,
    val data: TDirectoryData,
    val objectKeys: List<TKey>,
    val children: List<TDirectoryNode>
)