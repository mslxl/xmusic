package io.github.mslxl.xmusic.common.addon.processor

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import kotlinx.coroutines.channels.ReceiveChannel

interface CollectionProcessor {
     suspend fun getAllCollection(): ReceiveChannel<EntityCollectionIndex>
     suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection
     suspend fun getContent(entity: EntityCollectionIndex): ReceiveChannel<EntitySongIndex>
}

interface EditableCollectionProcessor : CollectionProcessor {
     fun add(target: EntityCollectionIndex, song: EntitySongIndex)
}