package io.github.mslxl.xmusic.desktop

import com.bulenkov.darcula.DarculaLaf
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.exitOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.cardLayout
import io.github.mslxl.ktswing.resizable
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.desktop.fs.LocalFile
import io.github.mslxl.xmusic.desktop.src.SourceLocalMusic
import io.github.mslxl.xmusic.desktop.ui.discoveryPane
import io.github.mslxl.xmusic.desktop.ui.playBar
import io.github.mslxl.xmusic.desktop.ui.settingsPane
import io.github.mslxl.xmusic.desktop.ui.sideBar
import java.awt.CardLayout
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.UIManager

object App {

    val core = XMusic(LocalFile()).apply {
        addMusicSource(SourceLocalMusic())
    }

    lateinit var cardLayout: CardLayout
    lateinit var centerPane: JPanel
    var currentShowCard = ""
    private fun showCard(cardName: String) {
        //TODO use slf4j instead of raw stdio
        println("Show card $cardName")
        cardLayout.show(centerPane, cardName)
        centerPane.updateUI()
        currentShowCard = cardName
    }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            UIManager.setLookAndFeel(DarculaLaf())
            JFrame.setDefaultLookAndFeelDecorated(true)
            JDialog.setDefaultLookAndFeelDecorated(true)
        } catch (e: Exception) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        }

        frame {
            attr {
                title = "XMusic"
                setSize(800, 500)
                resizable
                setLocationRelativeTo(null)
            }
            borderLayout {
                left {
                    add(
                        sideBar(
                            onDiscoveryAction = { showCard("Discovery") },
                            onMineAction = { showCard("My") },
                            onSettingAction = { showCard("Setting") }
                        ))
                }
                center {
                    panel {
                        centerPane = self

                        cardLayout = cardLayout {
                            card("Setting") {
                                add(settingsPane())
                            }
                            card("My") {
                                tabbedPane {
                                    tab("My") {
                                    }
                                }
                            }
                            card("Discovery") {
                                add(discoveryPane())
                            }
                            show("Discovery")
                        }
                    }
                }
                bottom {
                    add(playBar())
                }
            }
        }.exitOnClose

    }
}