package io.github.mslxl.xmusic.common.source.processor

/**
 * The class which impl [Explorable] means it can display on Explore
 */
interface Explorable

interface ExplorerProcessor<T : Explorable> {
    suspend fun refresh(): Sequence<T>
}
