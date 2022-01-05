package io.github.mslxl.xmusic.common.entity

import io.github.mslxl.xmusic.common.source.SourceID
import io.github.mslxl.xmusic.common.source.processor.Explorable
import java.util.*


/**
 * Store the root of information, program will query more information on the basis of this class.
 * The detail will store in local database, if it not exists, program will query Source.
 * Query result should be [EntitySongInfo]
 *
 * Attention! A [EntitySong] may have many parts, it's likes BiliBili's video parts.
 */
data class EntitySong(
    val id: String,
    val source: SourceID,
) : Explorable {
    val uuid by lazy {
        UUID.fromString("song$id$source")
    }
}