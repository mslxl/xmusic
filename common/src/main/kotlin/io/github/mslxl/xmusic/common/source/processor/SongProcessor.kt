package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo

interface SongProcessor {
    suspend fun getInfo(entitySong: EntitySong): EntitySongInfo
    suspend fun getURL(entitySong: EntitySong, option: String): String
    suspend fun getOption(entitySong: EntitySong): List<String> {
        return listOf("default")
    }
}