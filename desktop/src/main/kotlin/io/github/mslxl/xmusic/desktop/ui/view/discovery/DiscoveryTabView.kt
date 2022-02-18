package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.isSearchable
import io.github.mslxl.xmusic.common.addon.processor.ext.isAlbumSearchable
import io.github.mslxl.xmusic.common.addon.processor.ext.isCollectionSearchable
import io.github.mslxl.xmusic.common.addon.processor.ext.isSongSearchable
import io.github.mslxl.xmusic.desktop.ui.compoent.fvBox
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class DiscoveryTabView(val src: MusicSource, override val parent: View?) : View {
    private val controller = DiscoveryTabController(this)
    override val root = swing<JComponent> {
        fvBox {
            attr {
                border = BorderFactory.createEmptyBorder(10, 15, 5, 15)
            }
            if (src.isSearchable) {
                searchBar()
            }
            src.discovery?.forEach { (key, processor) ->
                addView(DiscoveryColumView(key, processor, src, this@DiscoveryTabView))
            }
        }

    }

    private fun CanAddChildrenScope<*>.searchBar() {
        panel {
            attr {
                maximumSize = maximumSize.apply {
                    height = 40
                }
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                )
            }
            borderLayout(10, 10) {
                center {
                    textField()
                }
                right {
                    hBox {
                        comboBox<String> {
                            if (src.isSongSearchable) self.addItem("Song")
                            if (src.isAlbumSearchable) self.addItem("Album")
                            if (src.isCollectionSearchable) self.addItem("Collection")
                        }
                        button("Search")
                    }
                }
            }
        }
    }
}