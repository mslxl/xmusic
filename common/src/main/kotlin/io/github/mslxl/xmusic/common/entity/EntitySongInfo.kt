package io.github.mslxl.xmusic.common.entity

/**
 * The detail information of a song
 */
data class EntitySongInfo(
    val parent: EntitySong,
    val id: String,
    val title: String,
    val singer: String,
    val coverUrl: String,
)
