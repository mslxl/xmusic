package io.github.mslxl.xmusic.desktop.ui.view.explorer

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.hBox
import io.github.mslxl.ktswing.component.imageLabel
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.entity.EntityAlbum
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.util.MusicUtils
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import io.github.mslxl.xmusic.desktop.ui.compoent.fvBox
import io.github.mslxl.xmusic.desktop.ui.util.scale
import io.github.mslxl.xmusic.desktop.ui.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JComponent

class ExplorerAlbumView(override val parent: View?, private val album: EntityAlbum) : View {
    val src = AddonsMan.getInstance<MusicSource>(album.index.sourceID)!!
    val list = JSongTable(src, App.core.playlist)

    init {
        CoroutineScope(Dispatchers.IO).launch() {
            val indexes = src.album!!.getContent(album.index)
            list.setDataSource(indexes)
        }
    }

    override val root: JComponent = swing {
        panelWith(borderLayout(10, 10)) {
            top {
                hBox {
                    attr {
                        border = BorderFactory.createEmptyBorder(50, 50, 50, 50)
                    }

                    glue
                    imageLabel {
                        attr {
                            val url = album.cover ?: MusicUtils.defaultCover.toURI().toURL()
                            icon = ImageIcon(url).scale(156, 156)
                        }
                    }
                    struct(64)
                    fvBox {
                        label(album.title) {
                            attr {
                                font = font.deriveFont(font.size + 24)
                            }
                        }
                        label(album.creator)
                        label(album.desc)
                    }
                    glue
                }
            }
            center {
                add(list)
            }
        }
    }
}