package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.xmusic.common.addon.hasDiscoveryPage
import io.github.mslxl.xmusic.common.manager.AddonsMan

class DiscoveryController(private val view: DiscoveryView) {
    val src = AddonsMan.addons.values.filter {
        it.hasDiscoveryPage
    }

}