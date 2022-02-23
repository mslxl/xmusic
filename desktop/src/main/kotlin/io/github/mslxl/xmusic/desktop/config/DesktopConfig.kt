package io.github.mslxl.xmusic.desktop.config

import io.github.mslxl.xmusic.common.config.ConfigurationFactory
import io.github.mslxl.xmusic.common.config.Default
import io.github.mslxl.xmusic.common.config.Expose
import io.github.mslxl.xmusic.common.config.UserConfiguration
import io.github.mslxl.xmusic.common.logger
import kotlin.concurrent.thread

interface DesktopConfig : UserConfiguration {
    companion object : DesktopConfig by ConfigurationFactory().buildFrom("desktop") {
        private val logger = DesktopConfig::class.logger

        init {
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                logger.info("commit desktop config")
                configuration.journal().commit()
            })
        }
    }

    @Expose
    @Default("Internal")
    var font: String

    @Expose
    @Default("12")
    var fontSize: Int

    @Expose
    @Default("50")
    var volume: Int

    @Expose
    @Default("false")
    var mute: Boolean
}