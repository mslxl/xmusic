package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.xmusic.common.manager.AddonsMan

class SettingController(private val view: SettingView) {
    val src = AddonsMan.addons
            .values
            .filter { it.configuration != null }
}