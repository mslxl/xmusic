package io.github.mslxl.xmusic.common.addon

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.processor.AlbumProcessor
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.addon.processor.SongProcessor
import io.github.mslxl.xmusic.common.addon.processor.ext.isAlbumSearchable
import io.github.mslxl.xmusic.common.addon.processor.ext.isCollectionSearchable
import io.github.mslxl.xmusic.common.addon.processor.ext.isSongSearchable
import io.github.mslxl.xmusic.common.config.UserConfiguration
import io.github.mslxl.xmusic.common.i18n.I18N
import io.github.mslxl.xmusic.common.i18n.I18NKey

/**
 * Provide information of song from different source
 *
 * [MusicSource] is just a basic of builder, it could create other processor that will
 * get the information of song. If a processor is not be supported, the processor field will be null
 */
typealias SourceID = String

interface MusicSource : I18N {
    val name: I18NKey
    override val id: SourceID
        get() = name.hashCode().toString()

    val userAgent: String
        get() = "XMusic/${XMusic.version}"

    val information: SongProcessor

    val discovery: Map<I18NKey, ExplorerProcessor<*, *>>?
        get() = null

    val album: AlbumProcessor?
        get() = null

    val collection: CollectionProcessor?
        get() = null

    val configuration: UserConfiguration?
        get() = null
}

val MusicSource.hasDiscoveryPage: Boolean
    get() = !discovery.isNullOrEmpty() || isSearchable


val MusicSource.isSearchable: Boolean
    get() = isCollectionSearchable || isSongSearchable || isAlbumSearchable

