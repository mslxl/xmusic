package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

fun playBar(): JPanel {
    return swing {
        panel {
            borderLayoutCenter {
                hBox {
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
                    }
                    // Next music
                    button("\uf105") {
                        awesomeFontSolid()
                    }
                    glue
                    // Audio volume
                    val CHAR_VOLUME_ON = "\uf028"
                    val CHAR_VOLUME_OFF = "\uf6a9"
                    button(CHAR_VOLUME_ON) {
                        awesomeFontSolid()
                    }
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