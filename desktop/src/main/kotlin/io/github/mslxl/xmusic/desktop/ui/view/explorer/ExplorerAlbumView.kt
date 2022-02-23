package io.github.mslxl.xmusic.desktop.ui.view.explorer

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.entity.EntityAlbum
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import io.github.mslxl.xmusic.desktop.ui.compoent.fvBox
import io.github.mslxl.xmusic.desktop.ui.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.swing.BorderFactory
import javax.swing.JComponent

class ExplorerAlbumView(override val parent: View?, private val album: EntityAlbum) : View {
    val src = AddonsMan.getInstance<MusicSource>(album.index.sourceID)!!
    val list = JSongTable(src, App.core.playlist)

    init {
        GlobalScope.launch(Dispatchers.IO) {
            val indexes = src.album!!.getContent(album.index)
            list.setDataSource(indexes)
        }
    }

    override val root: JComponent = swing {
        panelWith(borderLayout(10, 10)) {
            top {
                panel {
                    attr {
                        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    }
                    borderLayout {
                        left {
                            //TODO: Image will be here
                        }
                        center {
                            fvBox {
                                //TODO: The information of album will be here
                            }
                        }
                    }
                }
            }
            center {
                add(list)
            }
        }

    }
}