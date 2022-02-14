package io.github.mslxl.xmusic.desktop.config

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.fs.FileSystem
import java.awt.GraphicsEnvironment

class DesktopConfig(fs: FileSystem) {
    private val config = SourceConfig(fs, "desktop")
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
    private val defaultFontSize = 12
    var fontSize: Int
        get() = config.get("font_size", defaultFontSize.toString()).toIntOrNull() ?: defaultFontSize
        set(value) {
            config.set("font_size", value.toString())
        }

    fun save() {
        config.save()
    }
}