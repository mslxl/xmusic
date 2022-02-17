package io.github.mslxl.xmusic.common.addon.processor

import io.github.mslxl.xmusic.common.addon.entity.EntityAlbum
import io.github.mslxl.xmusic.common.addon.entity.EntityAlbumIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex

interface AlbumProcessor {
    suspend fun getContent(entityAlbumIndex: EntityAlbumIndex): Sequence<EntitySongIndex>
    suspend fun getInformation(entityAlbumIndex: EntityAlbumIndex): EntityAlbum
}