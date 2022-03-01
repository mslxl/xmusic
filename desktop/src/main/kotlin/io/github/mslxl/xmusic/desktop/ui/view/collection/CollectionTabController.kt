package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext

class CollectionTabController(val view: CollectionTabView) {
    private val src get() = view.src
    fun loadContent(collection: EntityCollection) {
        CoroutineScope(Dispatchers.IO).launch {
            val seq = src.collection!!.getContent(collection.index)
            withContext(Dispatchers.Swing) {
                view.rightTable.setDataSource(seq)
            }
        }
    }

    fun loadList() {
        // prepare current collection data
        CoroutineScope(Dispatchers.IO).launch {
            val allCollectionIndex = src.collection!!.getAllCollection()
            view.leftList.setData(allCollectionIndex)
        }
    }
}