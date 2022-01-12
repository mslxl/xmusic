package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

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