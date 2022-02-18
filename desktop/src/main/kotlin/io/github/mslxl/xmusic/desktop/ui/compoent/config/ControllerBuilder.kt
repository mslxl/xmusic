package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.xmusic.common.config.ConfigurationJournal
import io.github.mslxl.xmusic.common.config.Type
import io.github.mslxl.xmusic.common.logger

class ControllerBuilder {
    companion object {
        val logger = ControllerBuilder::class.logger
    }

    private val instances = hashMapOf<Type, ConfigItemComponentController>()

    /**
     * Single instance
     */
    fun getController(journal: ConfigurationJournal, key: String): ConfigItemComponentController {
        val type = journal.exposedKey[key]!!
        return instances.getOrPut(type) {
            createController(type)
        }.apply {
            setData(journal, key)
        }
    }

    /**
     * Not single instance
     */
    fun createController(
            type: Type
    ): ConfigItemComponentController {
        return when (type) {
            Type.Text -> TextController()
            Type.EncryptText -> PwdController()
            Type.FilePath -> TextController() //TODO add a file chooser for this type
            else -> error("Unknown config type $type")
        }
    }

    fun clear() {
        instances.clear()
    }
}