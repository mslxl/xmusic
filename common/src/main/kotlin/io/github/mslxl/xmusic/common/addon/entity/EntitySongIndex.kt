package io.github.mslxl.xmusic.common.addon.entity

import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
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