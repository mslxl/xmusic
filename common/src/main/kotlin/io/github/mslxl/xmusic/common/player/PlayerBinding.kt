package io.github.mslxl.xmusic.common.player

import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import java.io.File

interface PlayerBinding {
    fun play(file: File, info: EntitySong)
    fun stop()
    fun watchPlayEnd(watcher: () -> Unit)
}