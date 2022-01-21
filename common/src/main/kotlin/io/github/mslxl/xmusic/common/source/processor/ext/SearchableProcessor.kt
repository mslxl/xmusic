package io.github.mslxl.xmusic.common.source.processor.ext

import io.github.mslxl.xmusic.common.source.processor.Explorable

/**
 * [AlbumProcessor], [CollectionProcessor] or [SongProcessor] can impl this interface
 * to stress the processor can support search
 *
 * **Other class impl this interface would never take effect**
 */
interface SearchableProcessor<T : Explorable> {
    suspend fun search(title: String): Sequence<T>
}