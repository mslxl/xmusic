package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.ExplorableIndex
import java.util.*

/**
 * Your Collection
 */
data class EntityCollectionIndex(
    val id: String,
    val source: SourceID
) : ExplorableIndex<EntityCollection> {
    val uuid by lazy {
        UUID.fromString("song$id$source")
    }
}