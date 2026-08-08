package io.github.thisismeamir.rootkt.format.models.base

import io.github.thisismeamir.rootkt.format.models.block.Block

data class Record(
    val key: TKey,
    val block: Block
)