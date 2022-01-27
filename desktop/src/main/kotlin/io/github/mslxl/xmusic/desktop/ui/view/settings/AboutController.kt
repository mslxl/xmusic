package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.component.textArea
import io.github.mslxl.ktswing.disposeOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.logger.JTextLoggerAppender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class AboutController(private val view: AboutView) {
    fun showDebugInfoFrame() {
        frame {
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
    }

    fun loadLicense() {
        GlobalScope.launch(Dispatchers.IO) {
            val txt =
                this::class.java.getResourceAsStream("/licenses/list.txt")?.let { inStream ->
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
            withContext(Dispatchers.Swing) {
                view.licenseTextArea.text = txt
            }
        }
    }
}