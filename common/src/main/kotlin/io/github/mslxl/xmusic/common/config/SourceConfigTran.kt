package io.github.mslxl.xmusic.common.config

import java.util.*

class SourceConfigTran internal constructor(val before: SourceConfig) : SourceConfig(before.fs, before.id) {
    override var properties: Properties = Properties()
    fun commit() {
        before.properties = this.properties
        before.save()
    }

    override fun listAllMarks(): MutableSet<MutableMap.MutableEntry<String, ConfigItem>> = before.listAllMarks()

    fun trash() {
        this.properties = before.properties
    }
}