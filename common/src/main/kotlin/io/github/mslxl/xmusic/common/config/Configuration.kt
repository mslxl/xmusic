package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.manager.PlatformMan
import java.util.*

class Configuration internal constructor(
        override val id: SourceID,
        override val exposedKey: HashMap<String, Type> = hashMapOf(),
        override val exposedDefaultValue: HashMap<String, Any> = hashMapOf()) : IConfiguration {
    companion object {
        private val logger = Configuration::class.logger
    }

    internal val file = PlatformMan.fs.config.resolve("$id.properties")
    internal val properties = Properties()

    init {
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        }
        logger.info("load configuration $id")
        file.bufferedReader().use {
            properties.load(it)
        }
    }

    override fun get(key: String): Any? {
        return if (properties.containsKey(key))
            properties[key]
        else
            exposedDefaultValue[key]
    }

    inline fun <T> get(key: String, default: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return get(key) as? T ?: default.invoke()
    }

    override fun journal() = ConfigurationJournal(this)
}