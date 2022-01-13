package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

private val logger = logger("playbar")
fun playBar(): JPanel {
    return swing {
        panel {
            borderLayoutCenter {
                hBox {
                    button("L/S")
                    glue
                    button("Img")
                    glue
                    label("No playing") {
                        VlcjControl.addPlayInfoListener { info, _ ->
                            self.text = "${info.singer} - ${info.title}"
                        }
                    }
                    glue
                    slider {
                        attr {
                            maximum = Int.MAX_VALUE
                            value = 0
                        }
                        var stopUpdateTime = false
                        VlcjControl.addPlayingListener { _, _, progress, totalLength ->
                            if (!stopUpdateTime) {
                                self.maximum = totalLength.toInt()
                                self.value = progress.toInt()

                                self.toolTipText =
                                    "${progress / (60 * 60 * 60)}:${progress / (60 * 60)}/${totalLength / (60 * 60 * 60)}:${totalLength / (60 * 60)}"
                            }
                        }
                        self.addMouseListener(object : MouseAdapter() {
                            override fun mousePressed(e: MouseEvent?) {
                                stopUpdateTime = true
                            }

                            override fun mouseReleased(e: MouseEvent?) {
                                stopUpdateTime = false
                            }
                        })
                        self.addChangeListener { changeEvent ->
                            if (stopUpdateTime) {
                                VlcjControl.setPlaytime(self.value.toLong())
                            }
                        }
                    }
                    glue
                    // Previous music
                    button("\uf104") {
                        awesomeFontSolid()
                        onAction {
                            App.core.playlist.pre()
                        }
                    }
                    // Pause/ Play
                    val CHAR_PLAY = "\uf04b"
                    val CHAR_PAUSE = "\uf04c"
                    button(CHAR_PLAY) {
                        awesomeFontSolid()
                        VlcjControl.addPlayStatusChangListener { _, _, isPaused ->
                            self.text = if (isPaused) CHAR_PLAY else CHAR_PAUSE
                        }
                        onAction {
                            VlcjControl.togglePause()
                        }
                    }
                    // Next music
                    button("\uf105") {
                        awesomeFontSolid()
                        onAction {
                            App.core.playlist.next()
                        }
                    }
                    glue

                    audioButton()

                    // Meta
                    button("\uf552") {
                        awesomeFontSolid()
                    }
                    glue
                    // Playlist
                    button("\uf0cb") {
                        awesomeFontSolid()
                        var popup: Popup? = null
                        onAction {
                            //TODO hover the button to show playlist
                            popup = if (popup == null) {
                                val location = self.locationOnScreen
                                logger.info("Show playlist")
                                playListPopup(self, location.x + self.width, location.y).apply {
                                    show()
                                }
                            } else {
                                logger.info("Close playlist")
                                popup!!.hide()
                                null
                            }
                        }
                    }

                }
            }
        }
    }
}

fun audioPopPanel(currentVolume: Int, onChange: (Int) -> Unit) = swing<JComponent> {
    panel {
        attr {
            border = BorderFactory.createLineBorder(Color.GRAY)
        }
        borderLayoutCenter {
            slider(orient = JSlider.VERTICAL) {
                attr {
                    maximum = 100
                    value = currentVolume
                    preferredSize.height = 90
                }
                self.addChangeListener {
                    onChange.invoke(self.value)
                }
            }
        }
    }
}

//TODO optimize code
private fun CanAddChildrenScope<*>.audioButton() {
    // Audio volume
    val CHAR_VOLUME_ON = "\uf028"
    val CHAR_VOLUME_OFF = "\uf6a9"
    button(CHAR_VOLUME_ON) {
        VlcjControl.addVolumeChangeListener {
            self.toolTipText = "$it%"
        }


        self.addMouseListener(object : MouseAdapter() {
            var panel: JComponent? = null
            var popup: Popup? = null
            override fun mouseEntered(e: MouseEvent) {
                popup?.hide()

                panel = audioPopPanel(VlcjControl.volume, onChange = { volume ->
                    VlcjControl.volume = volume
                })

                panel!!.addMouseListener(object : MouseAdapter() {
                    override fun mouseExited(e: MouseEvent?) {
                        popup?.hide()
                    }
                })

                val popupFactory = PopupFactory.getSharedInstance()
                val btnLocation = self.locationOnScreen
                val panelX = btnLocation.x + (self.width / 2 - panel!!.preferredSize.width / 2)
                val panelY = btnLocation.y - panel!!.preferredSize.height + 5
                popup =
                    popupFactory.getPopup(self, panel, panelX, panelY)
                popup!!.show()
                panel!!.grabFocus()
            }

            override fun mouseExited(e: MouseEvent) {
                // 还有这种写法？
                if (panel?.hasFocus() == false) {
                    popup?.hide()
                    popup = null
                }

            }

            override fun mouseClicked(e: MouseEvent) {
                VlcjControl.toggleMute()
            }
        })
        VlcjControl.addMuteListener { isMute ->
            self.text = if (isMute) CHAR_VOLUME_OFF else CHAR_VOLUME_ON
        }
        awesomeFontSolid()
    }
}

fun playListPopup(parent: JComponent, x: Int, y: Int): Popup {

    val model = object : AbstractListModel<EntitySongInfo>() {
        override fun getSize(): Int = App.core.playlist.size


        override fun getElementAt(index: Int): EntitySongInfo =
            App.core.playlist.list[index]
    }


    val panel = swing<JComponent> {
        panel {
            attr {
                border = BorderFactory.createLineBorder(Color.GRAY)
                preferredSize.apply {
                    height = 400
                    width = 350
                }
            }
            borderLayoutCenter {
                scrollPane {
                    list<EntitySongInfo> {
                        val render = DefaultListCellRenderer()
                        self.cellRenderer =
                            ListCellRenderer {
                                //TODO add special icon for song in playing status
                                    list, value, index, isSelected, cellHasFocus ->
                                render.getListCellRendererComponent(
                                    list,
                                    "${value.singer} - ${value.title}",
                                    index,
                                    isSelected,
                                    cellHasFocus
                                )
                            }
                        self.model = model
                        self.selectionMode = ListSelectionModel.SINGLE_SELECTION
                        self.selectedIndex = App.core.playlist.currentPos
                        self.selectionModel.addListSelectionListener {
                            App.core.playlist.currentPos = self.selectedIndex
                        }
                    }
                }
            }
        }
    }

    val factory = PopupFactory.getSharedInstance()
    return factory.getPopup(parent, panel, x - panel.preferredSize.width, y - panel.preferredSize.height)
}