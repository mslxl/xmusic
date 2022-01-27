package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.desktop.App

class SettingController(private val view: SettingView) {
    val src = App.core.sourceList.map { App.core.getCfg(it) }.filter {
        it.listAllMarks().isNotEmpty()
    }
}