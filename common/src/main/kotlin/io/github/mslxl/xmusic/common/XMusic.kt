package io.github.mslxl.xmusic.common

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.fs.FileSystem
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.SourceID

class XMusic(val fs: FileSystem) {
    private val sources: HashMap<SourceID, MusicSource> = hashMapOf()
    private val config: HashMap<SourceID, SourceConfig> = hashMapOf()
    fun addMusicSource(src: MusicSource) {
        if (src.id in sources) {
            error("${src.id} is duplicated")
        }
        val cfg = SourceConfig(fs, src.id)
        src.acceptConfig(cfg)
        config[src.id] = cfg
        sources[src.id] = src
    }

    val sourceList get() = sources.keys

    fun getSrc(id: SourceID) = sources[id]!!
    fun getCfg(id: SourceID) = config[id]!!

    companion object {
        const val version = "0.0.1-alpha"
    }
}