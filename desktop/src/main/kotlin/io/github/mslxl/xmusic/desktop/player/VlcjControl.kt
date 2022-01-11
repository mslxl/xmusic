package io.github.mslxl.xmusic.desktop.player

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaRef
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.io.File
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

object VlcjControl {
    private val logger = VlcjControl::class.logger.apply {
        info("vlcj start init")
    }

    private val factory = MediaPlayerFactory()
    private val player = factory.mediaPlayers().newMediaPlayer()

    private var playingInfo: EntitySongInfo? = null
    private var playingFile: File? = null
    private var playingDuration: Long = -1
    private var playingPos: Long = 0

    init {
        Runtime.getRuntime().addShutdownHook(thread(start = false, name = "ShutdownHook-vlcj") {
            logger.info("Release vlcj")
            player.release()
        })

        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun mediaChanged(mediaPlayer: MediaPlayer?, media: MediaRef?) {
                logger.info("vlcj media change to ${playingFile?.absolutePath}")
                playingDuration = -1
            }

            override fun finished(mediaPlayer: MediaPlayer) {
                logger.info("vlcj play finish ${playingFile?.absolutePath}")
                playingDuration = -1
                playingFile = null
                playingInfo = null
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                logger.error("!!vlcj error!!")
            }

            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                playingPos = newTime
            }
        })
        player.events().addMediaEventListener(object : MediaEventAdapter() {
            override fun mediaDurationChanged(media: Media?, newDuration: Long) {
                if (playingDuration == -1L) {
                    logger.info("Media duration: $newDuration")
                    playingDuration = newDuration
                }
            }
        })

    }

    fun play(file: File, info: EntitySongInfo) {
        if (file.extension !in XMusic.acceptExt)
            error("Unsupported media file ${file.extension}")
        logger.info("Start to play ${file.absolutePath}")
        playingInfo = info
        playingFile = file
        thread(name = "Vlcj-playing") {
            player.media().play(file.absolutePath)
        }
    }

    val controlApi get() = player.media()

    fun addPlayInfoListener(listener: (info: EntitySongInfo, cacheFile: File) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun mediaChanged(mediaPlayer: MediaPlayer?, media: MediaRef?) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!)
                }
            }
        })
    }

    fun addPlayEndListener(listener: (info: EntitySongInfo, cacheFile: File) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun finished(mediaPlayer: MediaPlayer) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!)
                }
            }
        })
    }

    fun addPlayingListener(listener: (info: EntitySongInfo, cacheFile: File, progress: Long, totalLength: Long) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!, newTime, playingDuration)
                }
            }
        })
    }

    fun setPlaytime(position: Long) {
        logger.info("Media time is set to $position")
        player.controls().setTime(position)
    }
}