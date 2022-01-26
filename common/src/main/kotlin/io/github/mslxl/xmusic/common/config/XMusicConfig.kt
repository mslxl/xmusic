package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.fs.FileSystem
import java.awt.GraphicsEnvironment

class XMusicConfig(val fs: FileSystem) {
    private val config = SourceConfig(fs, "xmusic")
    val availableFont by lazy {
        arrayOf(
            arrayOf(defaultFont.first),
            GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        ).flatten()
    }
    val defaultFont = "Internal" to "/io/github/mslxl/xmusic/common/res/wqy-microhei.ttc"

    var font: String
        get() = config.get("font", defaultFont.first)
        set(value) {
            config.set("font", value)
        }

    fun save() {
        config.save()
    }
}