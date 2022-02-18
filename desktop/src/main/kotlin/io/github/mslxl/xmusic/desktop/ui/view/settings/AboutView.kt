package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.JPanel
import javax.swing.JTextArea

class AboutView(override val parent: View?) : View {
    private val controller = AboutController(this)
    val licenseTextArea: JTextArea
    override val root = swing<JPanel> {
        panel {
            borderLayout {
                top {
                    vBox {
                        label("XMusic desktop ${App.version}")
                        label("XMusic core ${XMusic.version}")
                        label("Licenses( include the open source project that XMusic depend on):")
                    }
                }
                center {
                    lazyPanelWith(borderLayoutCenter()) {
                        scrollPane {
                            licenseTextArea = textArea {
                                attr {
                                    isEditable = false
                                    text = "Loading..."
                                }
                            }
                        }
                        controller.loadLicense()
                    }
                }
                bottom {
                    panel {
                        borderLayout {
                            right {
                                button("Debug Info").addActionListener {
                                    controller.showDebugInfoFrame()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}