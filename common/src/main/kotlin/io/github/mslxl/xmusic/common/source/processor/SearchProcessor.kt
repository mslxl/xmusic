package io.github.mslxl.xmusic.common.source.processor

import io.github.mslxl.xmusic.common.entity.EntitySong

interface SearchProcessor {
    suspend fun search(title: String): Sequence<EntitySong>
}