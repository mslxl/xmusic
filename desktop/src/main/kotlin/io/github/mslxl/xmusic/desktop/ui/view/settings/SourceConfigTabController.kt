package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.common.logger

class SourceConfigTabController(private val view: SourceConfigTabView) {
    companion object {
        val logger = SourceConfigTabController::class.logger
    }

    var trans = view.config.journal()
    fun commit() {
        logger.info("journal ${view.config.id} config commit")
        trans.commit()
    }

    fun cancel() {
        trans.trash()
        logger.info("Trash modified config")
        view.configComponentList.reload(trans)
    }
}