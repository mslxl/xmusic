package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.player.VirtualPlaylist
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import io.github.mslxl.xmusic.desktop.ui.util.PopupPosition
import io.github.mslxl.xmusic.desktop.ui.util.linkHoverPopup
import io.github.mslxl.xmusic.desktop.ui.util.linkTogglePopup
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

private val logger = logger("playbar")
fun playBar(): JPanel {
    return swing {
        panel {
            borderLayoutCenter {
                hBox {
                    val MODE_SHUFFLE = "\uf074"
                    val MODE_ORDER = "\uf160"
                    val MODE_LOOP = "\uf2f9" //icon 'repeat' is pro feature, XD
                    val modes = listOf(MODE_ORDER, MODE_SHUFFLE, MODE_LOOP)
                    var currentMode = 0
                    button(modes[App.core.playlist.playMode.ordinal]) {
                        awesomeFontSolid()
                        onAction {
                            currentMode++
                            self.text = modes[currentMode % 3]
                            App.core.playlist.playMode = VirtualPlaylist.PlayMode.values()[currentMode % 3]
                        }
                    }
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
                            App.core.playlist.preByMode()
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
                            App.core.playlist.nextByMode()
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
                        linkTogglePopup(playListPopupPanel(), PopupPosition.DynamicCalc { btn, panel ->
                            val btnLoc = btn.locationOnScreen
                            val x = btnLoc.x + btn.width - panel.preferredSize.width
                            val y = btnLoc.y - panel.preferredSize.height
                            Point(x, y)
                        })
                    }
                }
            }
        }
    }
}

fun audioPopupPanel(currentVolume: Int, onChange: (Int) -> Unit) = swing<JComponent> {
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

private fun CanAddChildrenScope<*>.audioButton() {
    // Audio volume
    val CHAR_VOLUME_ON = "\uf028"
    val CHAR_VOLUME_OFF = "\uf6a9"
    button(CHAR_VOLUME_ON) {
        awesomeFontSolid()
        VlcjControl.addVolumeChangeListener {
            self.toolTipText = "$it%"
        }
        self.addActionListener {
            VlcjControl.toggleMute()
        }
        VlcjControl.addMuteListener { isMute ->
            self.text = if (isMute) CHAR_VOLUME_OFF else CHAR_VOLUME_ON
        }

        linkHoverPopup(audioPopupPanel(VlcjControl.volume, onChange = { newVolume ->
            VlcjControl.volume = newVolume
        }), PopupPosition.UpOwner())
    }
}


fun playListPopupPanel(): JComponent {
    val listModel = object : AbstractListModel<EntitySongInfo>() {
        val playlist = App.core.playlist
        var lastUpdateIdx = 0

        init {
            playlist.addListChangeListener {
                fireContentsChanged(playlist, 0, size)
            }
            playlist.addCurrentChangeListener {
                if (it == null) {
                    fireContentsChanged(playlist, 0, size)
                } else {
                    val idx = playlist.currentPos
                    fireContentsChanged(playlist, lastUpdateIdx, lastUpdateIdx)
                    fireContentsChanged(playlist, idx, idx)
                    lastUpdateIdx = idx
                }
            }
        }

        override fun getSize(): Int = playlist.size


        override fun getElementAt(index: Int): EntitySongInfo = playlist.list[index]

    }
    val listRenderer = object : ListCellRenderer<EntitySongInfo> {
        val playlist = App.core.playlist
        override fun getListCellRendererComponent(
            list: JList<out EntitySongInfo>?,
            value: EntitySongInfo?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ) = swing<JComponent> {
            panel {
                attr {
                    border = if (isSelected) BorderFactory.createLineBorder(Color.WHITE)
                    else BorderFactory.createEtchedBorder()
                }
                borderLayout {
                    left {
                        label(" ") {
                            awesomeFontSolid()
                            if (index == playlist.currentPos) {
                                self.text = "\uf001 "
                            } else {
                                self.text = "  "
                            }
                        }
                    }
                    center {
                        label("${value!!.singer} - ${value.title}")
                    }
                }
            }
        }
    }
    return swing {
        panel {
            attr {
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(15, 15, 10, 15)
                )
                preferredSize = Dimension(300, 400)
            }
            borderLayout {
                top {
                    panelWith(borderLayout()) {
                        top {
                            label("Playlist") {
                                attr {
                                    font = font.deriveFont(18.0f)
                                }
                            }
                        }
                        bottom {
                            panelWith(borderLayout()) {
                                right {
                                    button("Clear").addActionListener {
                                        App.core.playlist.clear()
                                        VlcjControl.stop()
                                    }
                                }
                            }
                        }
                    }
                }
                center {
                    scrollPane {
                        list<EntitySongInfo> {
                            attr {
                                model = listModel
                                cellRenderer = listRenderer
                            }
                            self.selectionModel.addListSelectionListener {
                                App.core.playlist.currentPos = self.selectedIndex
                                App.core.playlist.notifyCurrentChange()
                            }
                        }
                    }
                }
            }
        }
    }
}