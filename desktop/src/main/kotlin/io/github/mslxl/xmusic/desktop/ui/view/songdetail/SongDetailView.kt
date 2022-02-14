package io.github.mslxl.xmusic.desktop.ui.view.songdetail

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.i18n.i18n
import io.github.mslxl.xmusic.common.util.MusicUtils
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.util.scale
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JOptionPane

class SongDetailView(override val parent: View, val song: EntitySong) : View {
    private val controller = SongDetailController(this)
    override val root: JComponent = swing {
        panelWith(borderLayout()) {
            top {
                vBox {
                    attr {
                        border = BorderFactory.createCompoundBorder(
                                BorderFactory.createEtchedBorder(),
                                BorderFactory.createEmptyBorder(20, 20, 20, 20)
                        )
                    }
                    label(song.title) {
                        self.font = self.font.deriveFont(self.font.size * 1.5f)
                    }
                    struct(5)
                    label(song.singer)
                }
            }
            left {
                vBox {
                    glue
                    hBox {
                        glue
                        imageLabel {
                            self.icon = ImageIcon(song.cover ?: MusicUtils.defaultCover.toURI().toURL())
                                    .scale(256, 256)
                        }
                        glue
                    }
                    hBox {
                        attr {
                            border = BorderFactory.createCompoundBorder(
                                    BorderFactory.createEtchedBorder(),
                                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                            )
                        }
                        glue
                        button("detail.play".i18n(App.core, App.id)).addActionListener {
                            controller.playSong(song)
                        }

                        button("detail.fav".i18n(App.core, App.id)) {
                            // TODO 变化 unfav 与 fav
                        }
                        button("detail.share".i18n(App.core, App.id))
                        glue
                    }
                    glue
                }
            }
            center {
                panel {
                    attr {
                        border = BorderFactory.createCompoundBorder(
                                BorderFactory.createEtchedBorder(),
                                BorderFactory.createEmptyBorder(20, 20, 20, 20)
                        )
                    }
                    flowLayout {
                        label("unsupported".i18n(App.core, App.id, "Lyc"))
                    }
                }
            }
        }
    }
}