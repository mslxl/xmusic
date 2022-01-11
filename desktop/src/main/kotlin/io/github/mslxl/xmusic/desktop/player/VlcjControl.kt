package io.github.mslxl.xmusic.desktop.player

import io.github.mslxl.xmusic.common.logger
import uk.co.caprica.vlcj.factory.MediaPlayerFactory

object VlcjControl {
    private val logger = VlcjControl::class.logger.apply {
        info("vlcj start init")
    }

    private val factory = MediaPlayerFactory()
    private val player = factory.mediaPlayers().newMediaPlayer()


    fun play() {

    }

}