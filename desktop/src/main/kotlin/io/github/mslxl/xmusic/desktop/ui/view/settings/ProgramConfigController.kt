package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.common.XMusicConfiguration
import io.github.mslxl.xmusic.desktop.config.DesktopConfig
import java.awt.GraphicsEnvironment
import javax.swing.JOptionPane

class ProgramConfigController(private val view: ProgramConfigView) {
    val availableFont by lazy {
        arrayOf(
                arrayOf("Internal"),
                GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        ).flatten()
    }

    fun commit() {
        var needRestart = false

        with(DesktopConfig) {
            if (font != view.comboBoxFont.selectedItem) {
                font = view.comboBoxFont.selectedItem?.toString()
                        ?: configuration.exposedDefaultValue["font"]!!.toString()
                needRestart = true
            }
            val configFontSize = view.spinnerFontSize.value.toString().toInt()
            if (fontSize != configFontSize) {
                fontSize = configFontSize
                needRestart = true
            }
            configuration.journal().commit()
        }
        with(XMusicConfiguration) {
            if (language != view.textFieldLang.text) {
                language = view.textFieldLang.text
                needRestart = true
            }
            configuration.journal().commit()
        }
        if (needRestart) {
            JOptionPane.showMessageDialog(view.root, "Some settings need restart to take effect")
        }
    }
}