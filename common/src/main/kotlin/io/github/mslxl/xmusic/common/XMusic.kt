package io.github.mslxl.xmusic.common

import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.events.XMusicInitializationEvent
import io.github.mslxl.xmusic.common.events.XMusicPostinitializationEvent
import io.github.mslxl.xmusic.common.events.XMusicPreinitializationEvent
import io.github.mslxl.xmusic.common.i18n.I18NStorage
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.net.NetworkHandle
import io.github.mslxl.xmusic.common.platform.CacheIndexDBManager
import io.github.mslxl.xmusic.common.platform.FileSystemEnv
import io.github.mslxl.xmusic.common.player.PlayerBinding
import io.github.mslxl.xmusic.common.player.VirtualPlaylist
import io.github.mslxl.xmusic.common.src.SourceLocalMusic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class XMusic(
        val fs: FileSystemEnv,
        val controller: PlayerBinding,
        val cacheManager: CacheIndexDBManager
) {
    val playlist = VirtualPlaylist()
    val i18n = I18NStorage(this)

    init {
        this::class.logger.info("XMusic core($version) init")

        // bind playlist and player
        playlist.addCurrentChangeListener {
            it?.let { info ->
                controller.stop()
                val srcId = info.index.source
                val src = AddonsMan.getInstance<MusicSource>(srcId)!!
                CoroutineScope(Dispatchers.IO).launch() {
                    //TODO use option
                    val network = NetworkHandle.require(src.id)
                    val url = src.information.getURL(info, src.information.getOption(info).first())
                    val file = network.download(url)
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


    companion object {
        const val appID = "io.github.mslxl.xmusic"
        const val version = "0.0.1-alpha"

        //TODO add more ext
        val acceptExt = listOf("mp3", "mp4", "m4a", "m4s", "wav")
    }
}