package io.github.mslxl.xmusic.common.i18n

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.SourceID

class I18NStorage(val core: XMusic) {
    private val logger = I18NStorage::class.logger

    var local: String = core.programConfig.lang
    val lang get() = getLang(local)
    private val data: MutableMap<SourceID, Map<I18NKey, String>> = HashMap()
    private fun getLang(local: String): String {
        return local.substringBefore('_')
    }

    fun insert(src: I18N) {
        val textPair = if (src.i18n.containsKey(local)) {
            logger.info("install i18n text $local from ${src.id}")
            src.i18n[lang]!!.invoke()
        } else {
            src.i18n.keys.find { getLang(it) == lang }?.let {
                logger.info("install i18n text $lang from ${src.id}")
                src.i18n[it]!!.invoke()
            }
        }
        if (textPair == null) {
            logger.error("could find i18n text in source ${src.id}")
            return
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
