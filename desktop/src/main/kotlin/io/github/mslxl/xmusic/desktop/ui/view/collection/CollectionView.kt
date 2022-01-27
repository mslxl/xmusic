package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import javax.swing.JTabbedPane

class CollectionView : View {
    private val controller = CollectionController(this)
    override val root = swing<JTabbedPane> {
        tabbedPane {
            tabPanel("My") {

            }
            controller.sources.forEach { src ->
                tabPanelWith(src.name, borderLayoutCenter()) {
                    addView(CollectionTabView(src))
                }
            }
        }
    }
}
