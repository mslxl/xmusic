package io.github.mslxl.xmusic.common.source.processor.ext

import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.Explorable

/**
 * [AlbumProcessor] or [SongProcessor] can impl this interface
 * to stress the processor can support search
 *
 * **Other class impl this interface would never take effect**
 */
interface SearchableProcessor<T : Explorable> {
    suspend fun search(title: String): Sequence<T>
}


fun MusicSource.isAlbumSearchable() = this.album != null && this.album is SearchableProcessor<*>
fun MusicSource.isSongSearchable() = this.information is SearchableProcessor<*>
fun MusicSource.isCollectionSearchable() = this.collection != null && this.collection is SearchableProcessor<*>
