package io.github.mslxl.xmusic.common.player

import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import kotlin.random.Random

class VirtualPlaylist {
    companion object {
        private val logger = VirtualPlaylist::class.logger
    }

    private val data = arrayListOf<EntitySongInfo>()

    val list: List<EntitySongInfo> get() = data
    val size get() = list.size
    var currentPos: Int = -1
        get() = if (field < 0) -1
        else if (field >= data.size) data.size - 1
        else field
        set(value) {
            field = value
            notifyCurrentChange()
        }
    private val listChangeListener = arrayListOf<() -> Unit>()
    private val currentChangeListener = arrayListOf<(EntitySongInfo?) -> Unit>()

    private fun notifyListChange() {
        listChangeListener.forEach {
            try {
                it.invoke()
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }
    }

    private fun notifyCurrentChange() {
        val current = data.getOrNull(currentPos)
        currentChangeListener.forEach {
            try {
                it.invoke(current)
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }
    }

    private fun handleCurrentPos() {
        if (currentPos < 0 && data.isNotEmpty()) {
            currentPos = 0
        } else if (data.isEmpty()) {
            currentPos = -1
        }
    }

    fun clear() {
        data.clear()
        notifyListChange()
        handleCurrentPos()
    }

    fun addListChangeListener(listener: () -> Unit) {
        listChangeListener.add(listener)
    }

    fun addCurrentChangeListener(listener: (current: EntitySongInfo?) -> Unit) {
        currentChangeListener.add(listener)
    }

    fun replace(entitySongInfo: EntitySongInfo) {
        data.clear()
        data.add(entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }

    fun replace(entitySongInfo: List<EntitySongInfo>) {
        data.clear()
        data.addAll(entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }

    fun add(entitySongInfo: EntitySongInfo) {
        data.add(entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }

    fun add(entitySongInfo: List<EntitySongInfo>) {
        data.addAll(entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }

    fun addAfterCurrent(entitySongInfo: EntitySongInfo) {
        data.add(currentPos + 1, entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }

    fun addAfterCurrent(entitySongInfo: List<EntitySongInfo>) {
        data.addAll(currentPos + 1, entitySongInfo)
        notifyListChange()
        handleCurrentPos()
    }


    fun pre(): Int {
        currentPos = if (currentPos == 0) list.lastIndex else currentPos - 1
        return currentPos
    }

    fun next(): Int {
        currentPos = if (currentPos < list.lastIndex) currentPos + 1 else 0

        return currentPos
    }

    fun nextShuffle(): Int {
        currentPos = Random.nextInt(0, list.lastIndex)
        return currentPos
    }

    fun nextLoop(): Int {
        currentPos = currentPos // It means not modify currentPos, but notify listener that this value has changed
        return currentPos
    }
}