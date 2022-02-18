package io.github.mslxl.xmusic.common

import io.github.mslxl.xmusic.common.config.*

interface XMusicConfiguration : UserConfiguration {
    companion object : XMusicConfiguration by ConfigurationFactory().buildFrom("xmusic") {

    }

    @Expose
    @Default("en")
    @Select("zh_CN", "en")
    var language: String
}