package io.github.mslxl.xmusic.common.addon.entity

import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
import java.net.URL

data class EntityAlbum(
        val index: EntityAlbumIndex,
        val name: String,
        val desc: String,
        val creator: String,
        override val cover: URL? = null,
) : ExplorableEntity {
    override val title: String
        get() = name
}
