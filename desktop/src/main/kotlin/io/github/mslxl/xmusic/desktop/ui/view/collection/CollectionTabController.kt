package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.xmusic.common.entity.EntityCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext

class CollectionTabController(val view: CollectionTabView) {
    private val src get() = view.src
    fun loadContent(collection: EntityCollection) {
        GlobalScope.launch(Dispatchers.IO) {
            val seq = src.collection!!.getContent(collection.index)
            withContext(Dispatchers.Swing) {
                view.rightTable.setDataSource(seq)
            }
        }
    }

    fun loadList() {
        // prepare current collection data
        GlobalScope.launch(Dispatchers.IO) {
            val allCollectionIndex = src.collection!!.getAllCollection()
            view.leftList.setData(allCollectionIndex)
        }
    }
}