package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.Explorable
import java.util.*

/**
 * Your Collection
 */
data class EntityCollectionIndex(
    val id: String,
    val source: SourceID
) : Explorable {
    val uuid by lazy {
        UUID.nameUUIDFromBytes("song$id$source".toByteArray())
    }
}