package io.github.mslxl.xmusic.common.config

import io.github.mslxl.xmusic.common.addon.SourceID

interface IConfiguration {
    val id: SourceID
    val exposedKey: Map<String, Type>
    val exposedDefaultValue: Map<String, Any>

    fun get(key: String): Any?
    fun journal(): ConfigurationJournal
}