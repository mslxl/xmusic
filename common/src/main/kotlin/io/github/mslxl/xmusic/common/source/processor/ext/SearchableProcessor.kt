package io.github.mslxl.xmusic.common.source.processor.ext

import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.source.processor.ExplorableIndex

/**
 * [AlbumProcessor] or [SongProcessor] can impl this interface
 * to stress the processor can support search
 *
 * **Other class impl this interface would never take effect**
 */
interface SearchableProcessor<T : ExplorableIndex<E>, E : ExplorableEntity> {
    suspend fun search(title: String): Sequence<T>
}


val MusicSource.isAlbumSearchable get() = this.album != null && this.album is SearchableProcessor<*, *>
val MusicSource.isSongSearchable get() = this.information is SearchableProcessor<*, *>
val MusicSource.isCollectionSearchable get() = this.collection != null && this.collection is SearchableProcessor<*, *>
