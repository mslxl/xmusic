package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.desktop.App
import javax.swing.JOptionPane

class ProgramConfigController(private val view: ProgramConfigView) {
    fun commit() {
        with(App.core.programConfig) {
            var needRestart = false
            if (font != view.comboBoxFont.selectedItem) {
                font = view.comboBoxFont.selectedItem?.toString() ?: defaultFont.first
                needRestart = true
            }
            save()
            if (needRestart) {
                JOptionPane.showMessageDialog(view.root, "Some settings need restart to take effect")
            }
        }
    }
}