package io.github.mslxl.xmusic.common.addon.entity

import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
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

