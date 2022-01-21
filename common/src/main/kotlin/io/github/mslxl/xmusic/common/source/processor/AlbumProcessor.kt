package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntityAlbumIndex
import io.github.mslxl.xmusic.common.entity.EntityAlbum
import io.github.mslxl.xmusic.common.entity.EntitySongIndex

interface AlbumProcessor {
    suspend fun getContent(entityAlbumIndex: EntityAlbumIndex): Sequence<EntitySongIndex>
    suspend fun getInformation(entityAlbumIndex: EntityAlbumIndex): EntityAlbum
}