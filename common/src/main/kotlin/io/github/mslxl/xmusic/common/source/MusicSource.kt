package io.github.mslxl.xmusic.common.source

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.i18n.I18nKey
import io.github.mslxl.xmusic.common.source.processor.AlbumProcessor
import io.github.mslxl.xmusic.common.source.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import io.github.mslxl.xmusic.common.source.processor.SearchProcessor

/**
 * Provide information of song from different source
 *
 * [MusicSource] is just a basic of builder, it could create other processor that will
 * get the information of song. If a processor is not be supported, the processor field will be null
 */
typealias SourceID = String

interface MusicSource {
    val name: String
    val id: SourceID
        get() = name.hashCode().toString()

    val userAgent: String
        get() = "XMusic/${XMusic.version}"

    val information: SongProcessor

    val search: SearchProcessor?
        get() = null

    val discovery: Map<I18nKey, ExplorerProcessor<*>>?
        get() = null

    val album: AlbumProcessor?
        get() = null


}