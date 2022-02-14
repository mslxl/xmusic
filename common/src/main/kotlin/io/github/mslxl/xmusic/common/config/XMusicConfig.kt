package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.fs.FileSystem
import java.util.*

class XMusicConfig(val fs: FileSystem) {
    private val config = SourceConfig(fs, "core")

    var lang: String
        get() = config.get("lang", Locale.getDefault().language)
        set(value) {
            config.set("lang", value)
        }

    fun save() {
        config.save()
    }
}