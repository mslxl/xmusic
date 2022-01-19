package io.github.mslxl.xmusic.common.entity

import java.net.URL

data class EntityAlbumInfo(
    val name: String,
    val desc: String,
    val creator: String,
    val coverUrl: URL,
    val data: Map<String, Any> = emptyMap()
)
