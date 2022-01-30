package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.ExplorableIndex
import java.util.*


/**
 * Store the root of information, program will query more information on the basis of this class.
 * The detail will store in local database, if it not exists, program will query Source.
 * Query result should be [EntitySong]
 *
 */
data class EntitySongIndex(
    val id: String,
    val source: SourceID,
) : ExplorableIndex<EntitySong> {
    val uuid by lazy {
        UUID.nameUUIDFromBytes("song$id$source".toByteArray())
    }
}