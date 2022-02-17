package io.github.mslxl.xmusic.common.addon.processor

import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import java.net.URL

interface SongProcessor {
    suspend fun getDetail(entitySongPreview: EntitySongIndex): Sequence<EntitySong>

    /**
     * Get download URL of a music part, X-Music will download it later.
     */
    suspend fun getURL(info: EntitySong, option: String): URL

    /**
     * Get available option, e.g. Quality
     */
    suspend fun getOption(info: EntitySong): List<String> {
        return listOf("default")
    }
}