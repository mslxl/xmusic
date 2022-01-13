package io.github.mslxl.xmusic.common.player

import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import java.io.File

interface PlayerBinding {
    fun play(file: File, info: EntitySongInfo)
    fun stop()
    fun watchPlayEnd(watcher: () -> Unit)
}