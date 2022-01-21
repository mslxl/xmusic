package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.entity.EntitySong
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