package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import java.net.URL

interface SongProcessor {
    suspend fun getInfo(entitySong: EntitySong): EntitySongInfo

    /**
     * Get download URL of a music part, X-Music will download it later.
     */
    suspend fun getURL(info: EntitySongInfo, option: String): URL

    /**
     * Get available option, e.g. Quality
     */
    suspend fun getOption(info: EntitySongInfo): List<String> {
        return listOf("default")
    }
}