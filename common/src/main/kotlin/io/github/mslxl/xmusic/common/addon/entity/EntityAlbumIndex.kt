package io.github.mslxl.xmusic.common.addon.entity

import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import java.util.*

/**
 * The function of [EntityAlbumIndex] as same as [EntitySongIndex]
 * Its content will provide by database or source
 */

data class EntityAlbumIndex(
    val id: String,
    val sourceID: SourceID
) : ExplorableIndex<EntityAlbum> {
    val uuid by lazy {
        UUID.nameUUIDFromBytes("album$id$sourceID".toByteArray())
    }
}