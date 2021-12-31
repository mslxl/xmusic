package io.github.mslxl.xmusic.common.entity

/**
 * The detail information of a song
 */
data class EntitySongInfo(
    val title: String,
    val singer: String,
    val coverUrl: String,
    val data: Map<String, Any> = emptyMap()
)
