package io.github.mslxl.xmusic.common.entity

import java.net.URL

/**
 * The detail information of a song
 */
data class EntitySong(
    val parent: EntitySongIndex,
    val id: String,
    val title: String,
    val singer: String,
    val coverUrl: URL,
)
