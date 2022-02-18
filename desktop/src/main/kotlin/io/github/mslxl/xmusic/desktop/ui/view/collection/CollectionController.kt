package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.xmusic.common.manager.AddonsMan

class CollectionController(private val view: CollectionView) {

    val sources = AddonsMan.addons.values.filter {
        it.collection != null
    }
}