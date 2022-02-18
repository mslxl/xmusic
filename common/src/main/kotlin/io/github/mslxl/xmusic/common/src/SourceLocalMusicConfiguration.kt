package io.github.mslxl.xmusic.common.src

import io.github.mslxl.xmusic.common.config.Expose
import io.github.mslxl.xmusic.common.config.Type
import io.github.mslxl.xmusic.common.config.UserConfiguration

interface SourceLocalMusicConfiguration : UserConfiguration {
    @Expose(type = Type.FilePath)
    val path: String?
}