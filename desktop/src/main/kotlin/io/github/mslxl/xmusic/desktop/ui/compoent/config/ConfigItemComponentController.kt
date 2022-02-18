package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.xmusic.common.config.ConfigurationJournal
import javax.swing.JComponent

interface ConfigItemComponentController {
    fun setData(journal: ConfigurationJournal, itemKey: String)

    fun addEditStartListener(listener: () -> Unit)
    fun addEditEndListener(listener: () -> Unit)
    fun getComponent(): JComponent
}
