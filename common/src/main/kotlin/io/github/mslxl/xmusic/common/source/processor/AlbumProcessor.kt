package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntityAlbum
import io.github.mslxl.xmusic.common.entity.EntityAlbumInfo

interface AlbumProcessor {
    suspend fun getContent(entityAlbum: EntityAlbum): Sequence<EntityAlbum>
    suspend fun getInformation(entityAlbum: EntityAlbum): EntityAlbumInfo
}