package io.github.mslxl.xmusic.desktop.ui.subpage

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.disposeOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.JTextLoggerAppender
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

private fun showDebugInfoFrame() = frame {
    var listener: ((String) -> Unit)? = null
    attr {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                listener?.let { JTextLoggerAppender.unregisterNotifier(it) }
            }
        })
    }

    borderLayoutCenter {
        scrollPane {
            val area = textArea {
                attr {
                    isEditable = false
                }
            }
            listener = { event: String ->
                area.text = "${area.text}$event\n"
            }
            JTextLoggerAppender.registerNotifier(listener!!)
        }
    }
    attr {
        setSize(650, 400)
        title = "Log"
    }
}.disposeOnClose

fun aboutPage(): JComponent = swing<JComponent> {
    panel {
        borderLayout {
            top {
                vBox {
                    label("XMusic desktop ${App.version}")
                    label("XMusic core ${XMusic.version}")
                    label("Licenses( include the open source project that XMusic depend on):")
                    //TODO split it by name
                }
            }
            center {
                lazyPanelWith(borderLayoutCenter()) {
                    scrollPane {

                        textArea {
                            attr {
                                isEditable = false
                                text = "Loading..."
                            }
                            thread(name = "Licenses-loader") {
                                val txt = this::class.java.getResourceAsStream("/licenses/list.txt")?.let { inStream ->
                                    inStream.bufferedReader().readText().splitToSequence("\n").map(String::trim)
                                        .filter(String::isNotBlank).map {
                                            val input = this::class.java.getResourceAsStream("/licenses/$it")
                                            if (input == null) {
                                                logger("licenses").info("could not found $it in /licenses")
                                            }
                                            it to input
                                        }.filter { it.second != null }
                                        .sortedBy { it.first }
                                        .map {
                                            logger("licenses").info("read license: ${it.first}")
                                            it.first to it.second!!.bufferedReader().readText()
                                        }.fold("") { acc, s ->
                                            "${"#".repeat(20)} ${s.first} ${"#".repeat(20)}\n${s.second}\n$acc"
                                        }

                                } ?: "Unavailable in debug mode: list file not found"

                                SwingUtilities.invokeAndWait {
                                    self.text = txt
                                }
                            }
                        }
                    }
                }
            }
            bottom {
                panel {
                    borderLayout {
                        right {
                            button("Debug Info").addActionListener {
                                showDebugInfoFrame()
                            }
                        }
                    }
                }
            }
        }
    }
}
