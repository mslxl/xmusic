package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.desktop.App
import javax.swing.JOptionPane

class ProgramConfigController(private val view: ProgramConfigView) {
    fun commit() {
        var needRestart = false

        with(App.desktopConfig) {
            if (font != view.comboBoxFont.selectedItem) {
                font = view.comboBoxFont.selectedItem?.toString() ?: defaultFont.first
                needRestart = true
            }
            val configFontSize = view.spinnerFontSize.value.toString().toInt()
            if (fontSize != configFontSize) {
                fontSize = configFontSize
                needRestart = true
            }
            save()
        }
        with(App.core.coreConfig) {
            if (lang != view.textFieldLang.text) {
                lang = view.textFieldLang.text
                needRestart = true
            }
            save()
        }
        if (needRestart) {
            JOptionPane.showMessageDialog(view.root, "Some settings need restart to take effect")
        }
    }
}