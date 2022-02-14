package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.processor.ExplorableEntity
import java.net.URL

/**
 * The detail information of a song
 */
data class EntitySong(
        val index: EntitySongIndex,
        val id: String,
        override val title: String,
        val singer: String,
        override val cover: URL? = null,
) : ExplorableEntity

