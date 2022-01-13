package io.github.mslxl.xmusic.desktop.player

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.player.PlayerBinding
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.Media
import uk.co.caprica.vlcj.media.MediaEventAdapter
import uk.co.caprica.vlcj.media.MediaRef
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.io.File
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

object VlcjControl : PlayerBinding {
    private val logger = VlcjControl::class.logger.apply {
        info("vlcj start init")
    }

    private val factory = MediaPlayerFactory()
    private val player = factory.mediaPlayers().newMediaPlayer()

    private var playingInfo: EntitySongInfo? = null
    private var playingFile: File? = null
    private var playingDuration: Long = -1
    private var playingPos: Long = 0

    var playingThread: Thread? = null

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
//                playingFile = null
//                playingInfo = null
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

    override fun play(file: File, info: EntitySongInfo) {
        if (file.extension !in XMusic.acceptExt)
            error("Unsupported media file ${file.extension}")
        logger.info("Prepare to play ${file.absolutePath}")
        playingInfo = info
        playingFile = file
        if (playingThread != null && playingThread!!.isAlive) {
            logger.info("Interrupt play thread before")
            try {
                playingThread!!.interrupt()
            } catch (e: Exception) {
                logger.error("Interrupt fail", e)
            }
        }
        playingThread = thread(name = "Vlcj-playing") {
            player.controls().stop()
            player.media().play(file.absolutePath)
        }
    }

    override fun stop() {
        player.controls().stop()
    }

    override fun watchPlayEnd(watcher: () -> Unit) {
        addPlayEndListener { info, cacheFile ->
            watcher.invoke()
        }
    }


    fun addPlayInfoListener(listener: (info: EntitySongInfo, cacheFile: File) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun mediaChanged(mediaPlayer: MediaPlayer?, media: MediaRef?) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!)
                }
            }
        })
    }

    fun addPlayStatusChangListener(listener: (info: EntitySongInfo, cacheFile: File, isPaused: Boolean) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!, false)
                }
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                SwingUtilities.invokeLater {
                    listener.invoke(playingInfo!!, playingFile!!, true)
                }
            }
        })

    }

    fun addPlayEndListener(listener: (info: EntitySongInfo, cacheFile: File) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun finished(mediaPlayer: MediaPlayer) {
                val (fst, snd) = (playingInfo!! to playingFile!!)
                SwingUtilities.invokeLater {
                    listener.invoke(fst, snd)
                }
            }
        })
    }

    fun addPlayingListener(listener: (info: EntitySongInfo, cacheFile: File, progress: Long, totalLength: Long) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                val (fst, snd) = (playingInfo!! to playingFile!!)
                SwingUtilities.invokeLater {
                    listener.invoke(fst, snd, newTime, playingDuration)
                }
            }
        })
    }

    fun addVolumeChangeListener(listener: (Int) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun volumeChanged(mediaPlayer: MediaPlayer?, volume: Float) {
                SwingUtilities.invokeLater {
                    listener.invoke((volume * 100).toInt())
                }
            }
        })
    }

    var volume: Int
        get() = player.audio().volume()
        set(value) {
            player.audio().setVolume(value)
        }

    fun setPlaytime(position: Long) {
        player.controls().setTime(position)
    }

    fun pause() = player.controls().pause()
    fun play() = player.controls().play()
    fun togglePause() {
        player.controls().setPause(player.status().isPlaying)
    }

    fun toggleMute() {
        player.audio().let {
            it.isMute = !it.isMute
        }
    }

    fun addMuteListener(listener: (isMute: Boolean) -> Unit) {
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun muted(mediaPlayer: MediaPlayer?, muted: Boolean) {
                SwingUtilities.invokeLater {
                    listener.invoke(muted)
                }
            }
        })
    }
}