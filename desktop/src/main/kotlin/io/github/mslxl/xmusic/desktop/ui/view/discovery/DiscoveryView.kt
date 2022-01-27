package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import javax.swing.JTabbedPane

class DiscoveryView : View {
    private val controller = DiscoveryController(this)
    override val root = swing<JTabbedPane> {
        tabbedPane {
            controller.src.forEach {
                tab(it.name) {
                    lazyPanelWith(borderLayoutCenter()) {
                        scrollPane {
                            addView(DiscoveryTabView(it))
                        }
                    }
                }
            }
        }
    }
}



