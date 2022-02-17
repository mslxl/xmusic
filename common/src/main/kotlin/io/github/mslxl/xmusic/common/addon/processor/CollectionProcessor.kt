package io.github.mslxl.xmusic.common.addon.processor

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex

interface CollectionProcessor {
    suspend fun getAllCollection(): Sequence<EntityCollectionIndex>
    suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection
    suspend fun getContent(entity: EntityCollectionIndex): Sequence<EntitySongIndex>
}

interface EditableCollectionProcessor : CollectionProcessor {
    suspend fun add(target: EntityCollectionIndex, song: EntitySongIndex)
}