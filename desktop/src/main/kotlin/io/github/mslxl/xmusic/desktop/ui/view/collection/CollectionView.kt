package io.github.mslxl.xmusic.desktop.ui.view.collection

import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.i18n.i18n
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import javax.swing.JTabbedPane

class CollectionView : View {
    private val controller = CollectionController(this)
    override val root = swing<JTabbedPane> {
        tabbedPane {
            tabPanel("my.my".i18n(App.core, App.id)) {

            }
            controller.sources.forEach { src ->
                tabPanelWith(src.name.i18n(App.core, src.id), borderLayoutCenter()) {
                    addView(CollectionTabView(src))
                }
            }
        }
    }
}
