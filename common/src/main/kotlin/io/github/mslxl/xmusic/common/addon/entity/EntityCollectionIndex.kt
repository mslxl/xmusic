package io.github.mslxl.xmusic.common.addon.entity

import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import java.util.*

/**
 * Your Collection
 */
data class EntityCollectionIndex(
    val id: String,
    val source: SourceID
) : ExplorableIndex<EntityCollection> {
    val uuid by lazy {
        UUID.nameUUIDFromBytes("song$id$source".toByteArray())
    }
}