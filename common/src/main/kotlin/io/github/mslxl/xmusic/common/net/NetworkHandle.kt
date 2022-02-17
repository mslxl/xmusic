package io.github.mslxl.xmusic.common.net

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.logger
import java.io.File
import java.net.URL

class NetworkHandle(val core: XMusic) {
    private val logger = NetworkHandle::class.logger

    @JvmOverloads
    fun download(src: MusicSource, url: URL,  allowCache: Boolean = true): File {
        logger.info("Receive download request \"$url\" from source ${src.name}(${src.id})")
        if (url.protocol == "file") {
            logger.info("Download finish: \"$url\" is already a local file")
            return File(url.toURI())
        }
        TODO()
    }
}