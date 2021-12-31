package io.github.mslxl.xmusic.common.entity

data class EntityAlbumInfo(
    val name: String,
    val desc: String,
    val creator: String,
    val coverUrl: String,
    val data: Map<String, Any> = emptyMap()
) {
}