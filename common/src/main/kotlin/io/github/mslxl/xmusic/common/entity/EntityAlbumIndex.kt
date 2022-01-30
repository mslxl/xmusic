package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.ExplorableIndex
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