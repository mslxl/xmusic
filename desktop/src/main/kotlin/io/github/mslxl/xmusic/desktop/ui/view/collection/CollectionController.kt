package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.xmusic.desktop.App

class CollectionController(private val view: CollectionView) {
    val sources = App.core.sourceList.map {
        App.core.getSrc(it)
    }.filter {
        it.collection != null
    }
}