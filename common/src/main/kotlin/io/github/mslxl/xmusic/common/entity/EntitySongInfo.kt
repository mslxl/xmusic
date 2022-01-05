package io.github.mslxl.xmusic.common.entity

/**
 * The detail information of a song
 */
data class EntitySongInfo(
    val id: String,
    val title: String,
    val singer: String,
    val coverUrl: String,
    val startPosition: Long = 0,
    val endPosition: Long = -1,
    val data: Map<String, Any> = emptyMap()
)
