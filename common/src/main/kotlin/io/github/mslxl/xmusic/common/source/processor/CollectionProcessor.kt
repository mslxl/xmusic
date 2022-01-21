package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.entity.EntitySongIndex

interface CollectionProcessor {
    suspend fun getAllCollection(): Sequence<EntityCollectionIndex>
    suspend fun getName(entity: EntityCollectionIndex): String
    suspend fun getContent(entity: EntityCollectionIndex): Sequence<EntitySongIndex>
}

interface EditableCollectionProcessor : CollectionProcessor {
    suspend fun add(target: EntityCollectionIndex, song: EntitySongIndex)
}