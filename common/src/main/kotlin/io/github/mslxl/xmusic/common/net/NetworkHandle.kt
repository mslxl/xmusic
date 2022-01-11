package io.github.mslxl.xmusic.common.net

import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import java.io.File
import java.net.URL

object NetworkHandle {
    private val logger = NetworkHandle::class.logger
    fun download(src: MusicSource, url: URL): File {
        logger.info("Receive download request \"$url\" from source ${src.name}(${src.id})")
        if (url.protocol == "file") {
            logger.info("Download finish: \"$url\" is already a local file")
            return File(url.toURI())
        }
        TODO()
    }
}