package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.xmusic.common.source.hasDiscoveryPage
import io.github.mslxl.xmusic.desktop.App

class DiscoveryController(private val view: DiscoveryView) {
    val src = App.core.sourceList.map {
        App.core.getSrc(it)
    }.filter {
        it.hasDiscoveryPage
    }

}