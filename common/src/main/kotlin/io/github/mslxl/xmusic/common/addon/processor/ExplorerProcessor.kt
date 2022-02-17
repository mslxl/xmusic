package io.github.mslxl.xmusic.common.addon.processor

import java.net.URL

/**
 * The class which impl [ExplorableIndex] means it can display on Explore
 */
interface ExplorableIndex<Entity : ExplorableEntity>

interface ExplorableEntity {
    val title: String
    val cover: URL?
}

interface ExplorerProcessor<T : ExplorableIndex<E>, E : ExplorableEntity> {
    suspend fun getExploredList(): Sequence<T>
    suspend fun getExploredDetail(index: T): Sequence<E>
}
