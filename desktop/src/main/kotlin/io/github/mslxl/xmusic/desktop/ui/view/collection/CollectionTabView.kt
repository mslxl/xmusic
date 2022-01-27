package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.split2Pane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JCollectionList
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.JPanel

class CollectionTabView(val src: MusicSource) : View {
    private val controller = CollectionTabController(this)
    val leftList = JCollectionList(src.collection!!).apply {
        addSelectionChangeListener { _, entityCollection ->
            if (entityCollection != null) {
                controller.loadContent(entityCollection)
            }
        }
    }
    val rightTable = JSongTable(src, App.core.playlist)
    override val root = swing<JPanel> {
        lazyPanel {
            borderLayoutCenter {
                split2Pane {
                    self.dividerSize = 2
                    left {
                        add(leftList)
                    }
                    right {
                        add(rightTable)
                    }
                    controller.loadList()
                }
            }
        }
    }
}