package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import io.github.mslxl.xmusic.common.logger

class ControllerBuilder {
    companion object {
        val logger = ControllerBuilder::class.logger
    }

    private val instances = hashMapOf<SourceConfig.ItemType, ConfigItemComponentController>()

    /**
     * Single instance
     */
    fun getController(trans: SourceConfigTran, itemIndex: SourceConfig.ItemIndex): ConfigItemComponentController {
        return instances.getOrPut(itemIndex.type) {
            createController(itemIndex)
        }.apply {
            setData(trans, itemIndex)
        }
    }

    /**
     * Not single instance
     */
    fun createController(
        itemIndex: SourceConfig.ItemIndex
    ): ConfigItemComponentController {
        return when (itemIndex.type) {
            SourceConfig.ItemType.TEXT -> TextController()
            SourceConfig.ItemType.PASSWORD -> PwdController()
            else -> error("Unknown config type ${itemIndex.type}")
        }
    }

    fun clear() {
        instances.clear()
    }
}