package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntityCollection
import io.github.mslxl.xmusic.common.entity.EntitySong

interface CollectionProcessor {
    suspend fun getAllCollection(): Sequence<EntityCollection>
    suspend fun getName(entity: EntityCollection):String
    suspend fun getContent(entity: EntityCollection): Sequence<EntitySong>
}