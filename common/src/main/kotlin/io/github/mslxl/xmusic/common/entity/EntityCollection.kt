package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.processor.ExplorableEntity
import java.net.URL

data class EntityCollection(
    val index: EntityCollectionIndex,
    val name: String,
    val desc: String,
    val creator: String,
    override val cover: URL? = null
) : ExplorableEntity {
    override val title: String
        get() = name
}
