package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.XMusic
import java.util.*

class ConfigurationJournal(val configuration: Configuration) : IConfiguration by configuration {

    private val properties = Properties()

    override fun get(key: String): Any? {
        return if (key in properties) {
            properties[key]
        } else {
            configuration.get(key)
        }
    }

    inline fun <T> get(key: String, default: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return get(key) as? T ?: default.invoke()
    }

    fun set(key: String, value: Any) {
        properties[key] = value.toString()
    }

    fun commit() {
        properties.forEach {
            configuration.properties[it.key] = it.value
        }
        configuration.file.bufferedWriter().use {
            configuration.properties.store(it, "Last save by core ${XMusic.version}")
        }
    }

    fun trash() {
        properties.clear()
    }

    override fun journal(): ConfigurationJournal = this

}