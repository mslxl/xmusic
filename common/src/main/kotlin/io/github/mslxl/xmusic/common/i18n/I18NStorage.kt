package io.github.mslxl.xmusic.common.i18n

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.XMusicConfiguration
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.logger

class I18NStorage(val core: XMusic) {
    private val logger = I18NStorage::class.logger


    var local: String = XMusicConfiguration.language
    val lang get() = getLang(local)
    private val data: MutableMap<SourceID, Map<I18NKey, String>> = HashMap()
    private fun getLang(local: String): String {
        return local.substringBefore('_')
    }

    fun insert(src: I18N) {
        val textPair = if (src.i18n.containsKey(local)) {
            logger.info("install i18n text $local from ${src.id}")
            src.i18n[local]!!.invoke()
        } else {
            src.i18n.keys.find { getLang(it) == lang }?.let {
                logger.info("install i18n text $lang from ${src.id}")
                src.i18n[it]!!.invoke()
            } ?: kotlin.run {
                logger.warn("fallback to English language")
                src.i18n["en"]!!.invoke()
            }
        }


        data[src.id] = textPair.toMap()
    }

    fun getString(src: MusicSource, key: I18NKey, vararg format: String): String {
        return getString(src.id, key, *format)
    }

    fun getString(src: SourceID, key: I18NKey, vararg format: String): String {
        if (!data.containsKey(src)) return key.format(*format)
        return (data[src]?.get(key) ?: key).format(*format)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun I18NKey.i18n(core: XMusic, src: SourceID, vararg format: String) = core.i18n.getString(src, this, *format)
