package io.github.mslxl.xmusic.desktop.config

import io.github.mslxl.xmusic.common.config.ConfigurationFactory
import io.github.mslxl.xmusic.common.config.Default
import io.github.mslxl.xmusic.common.config.Expose
import io.github.mslxl.xmusic.common.config.UserConfiguration

interface DesktopConfig : UserConfiguration {
    companion object : DesktopConfig by ConfigurationFactory().buildFrom("desktop") {

    }

    @Expose
    @Default("Internal")
    var font: String

    @Expose
    @Default("12")
    var fontSize: Int
}