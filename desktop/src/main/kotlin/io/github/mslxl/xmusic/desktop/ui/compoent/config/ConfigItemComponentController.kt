package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import javax.swing.JComponent

interface ConfigItemComponentController {
    fun setData(trans: SourceConfigTran, itemIndex: SourceConfig.ItemIndex)
    fun addEditStartListener(listener: () -> Unit)
    fun addEditEndListener(listener: () -> Unit)
    fun getComponent(): JComponent
}
