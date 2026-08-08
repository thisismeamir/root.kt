package io.github.thisismeamir.rootkt.format.models.directory

import io.github.thisismeamir.rootkt.format.models.base.TKey

data class TDirectoryNode(
    val key: TKey,              // null for the top-level TFile "directory"
    val data: TDirectoryData,
    val objectKeys: List<TKey>,      // non-directory keys in this directory
    val children: List<TDirectoryNode>  // recursively parsed subdirectories
)

