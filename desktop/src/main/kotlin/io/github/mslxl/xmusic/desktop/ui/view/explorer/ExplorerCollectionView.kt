package io.github.mslxl.xmusic.desktop.ui.view.explorer

import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import io.github.mslxl.xmusic.desktop.ui.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.swing.JComponent

class ExplorerCollectionView(override val parent: View?, val entityCollection: EntityCollection) : View {
    val src = AddonsMan.getInstance<MusicSource>(entityCollection.index.source)!!
    val list = JSongTable(src, App.core.playlist)
    override val root: JComponent = swing {
        panelWith(borderLayoutCenter()) {
            add(list)
        }
        GlobalScope.launch(Dispatchers.IO) {
            val songIndexes = src.collection!!.getContent(entity = entityCollection.index)
            list.setDataSource(songIndexes)
        }
    }
}