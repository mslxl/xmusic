package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.isSearchable
import io.github.mslxl.xmusic.common.source.processor.ext.isAlbumSearchable
import io.github.mslxl.xmusic.common.source.processor.ext.isCollectionSearchable
import io.github.mslxl.xmusic.common.source.processor.ext.isSongSearchable
import io.github.mslxl.xmusic.desktop.ui.view.View
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JPanel

class DiscoveryTabView(val src: MusicSource) : View {
    private val controller = DiscoveryTabController(this)
    override val root = swing<JPanel> {
        vBox {
            attr {
                border = BorderFactory.createEmptyBorder(10, 15, 5, 15)
            }
            if (src.isSearchable) {
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
            // TODO
            repeat(10) {
                panel {
                    attr {
                        border = BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10, 10, 10, 10),
                            BorderFactory.createTitledBorder("Discovery catalog: Not impl")
                        )
                    }
                    borderLayoutCenter {
                        label("TODO: id $it, will parse data form data source")
                    }
                }
            }
        }
    }
}