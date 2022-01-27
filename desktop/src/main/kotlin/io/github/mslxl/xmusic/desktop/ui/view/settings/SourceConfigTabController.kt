package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.common.logger

class SourceConfigTabController(private val view: SourceConfigTabView) {
    companion object {
        val logger = SourceConfigTabController::class.logger
    }

    var trans = view.config.transact()
    fun commit() {
        logger.info("${view.config.id} config commit")
        trans.commit()
    }

    fun cancel() {
        logger.info("Trash modified config")
        trans = view.config.transact()
        view.configComponentList.reload(trans)
    }
}