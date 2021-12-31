package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.Explorable
import java.util.*

/**
 * The function of [EntityAlbum] as same as [EntitySong]
 * Its content will provide by database or source
 */

data class EntityAlbum(
    val id: String,
    val sourceID: SourceID
) : Explorable {
    val uuid by lazy {
        UUID.fromString("album$id$sourceID")
    }
}