package io.github.mslxl.xmusic.common

import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.XMusicConfig
import io.github.mslxl.xmusic.common.events.XMusicInitializationEvent
import io.github.mslxl.xmusic.common.events.XMusicPostinitializationEvent
import io.github.mslxl.xmusic.common.events.XMusicPreinitializationEvent
import io.github.mslxl.xmusic.common.fs.CacheIndexDBManager
import io.github.mslxl.xmusic.common.fs.FileSystem
import io.github.mslxl.xmusic.common.i18n.I18NStorage
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.net.NetworkHandle
import io.github.mslxl.xmusic.common.player.PlayerBinding
import io.github.mslxl.xmusic.common.player.VirtualPlaylist
import io.github.mslxl.xmusic.common.src.SourceLocalMusic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class XMusic(
        val fs: FileSystem,
        val controller: PlayerBinding,
        val cacheManager: CacheIndexDBManager
) {
    val playlist = VirtualPlaylist()
    val network = NetworkHandle(this)
    val coreConfig = XMusicConfig(fs)
    val i18n = I18NStorage(this)

    init {
        this::class.logger.info("XMusic core($version) init")

        // bind playlist and player
        playlist.addCurrentChangeListener {
            it?.let { info ->
                controller.stop()
                val srcId = info.index.source
                val src = AddonsMan.getInstance<MusicSource>(srcId)!!
                GlobalScope.launch(Dispatchers.IO) {
                    //TODO use option
                    val url = src.information.getURL(info, src.information.getOption(info).first())
                    val file = network.download(src, url)
                    controller.play(file, info)
                }
            }
        }

        controller.watchPlayEnd {
            playlist.next()
        }

        AddonsMan.register(SourceLocalMusic::class)
    }

    fun startInit() {
        AddonsMan.sentEvent(XMusicPreinitializationEvent(this))
        AddonsMan.sentEvent(XMusicInitializationEvent(this))
        AddonsMan.sentEvent(XMusicPostinitializationEvent(this))
    }

    private val config: HashMap<SourceID, SourceConfig> = hashMapOf()

    fun getCfg(id: SourceID) = config[id]!!

    companion object {
        const val appID = "io.github.mslxl.xmusic"
        const val version = "0.0.1-alpha"

        //TODO add more ext
        val acceptExt = listOf("mp3", "mp4", "m4a", "m4s")
    }
}