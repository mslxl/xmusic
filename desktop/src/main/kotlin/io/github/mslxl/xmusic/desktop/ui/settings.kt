package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.subpage.aboutPage
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTabbedPane

fun settingsPane(): JTabbedPane {
    val logger = logger("Settings Pane")
    val src = App.core.sourceList.map { App.core.getCfg(it) }.filter {
        it.listAllMarks().isNotEmpty()
    }

    return swing {
        tabbedPane {
            src.forEach {
                val srcName = App.core.getSrc(it.id).name
                tabPanelWith(srcName, borderLayoutCenter()) {

                    lazyPanel {
                        logger.info("$srcName config page created")
                        borderLayout {
                            center {
                                scrollPane {
                                    vBox {
                                        val trans = it.transact()
                                        trans.listAllMarks().forEachIndexed { index, entry ->
                                            val pane = when (entry.value.type) {
                                                SourceConfig.ItemType.TEXT -> cfgTextField(
                                                    trans,
                                                    entry.key,
                                                    entry.value
                                                )
                                                SourceConfig.ItemType.PASSWORD -> cfgPwdField(
                                                    trans,
                                                    entry.key,
                                                    entry.value
                                                )
                                                else -> error("Unknown config type ${entry.value.type}")
                                            }
                                            add(pane)
                                            glueAround
                                        }
                                        hBox {
                                            button("Cancel").addActionListener {
                                                JOptionPane.showMessageDialog(this.self, "TODO")
                                            }
                                            glue
                                            button("Apply").addActionListener {
                                                logger.info("$srcName config commit")
                                                trans.commit()
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }
                }
            }

            tabPanelWith("About", borderLayoutCenter()) {
                add(aboutPage())
            }
        }
    }
}



private fun cfgTextField(config: SourceConfigTran, key: String, item: SourceConfig.ConfigItem): JPanel {
    val currentValue = config.getNullable(key) ?: ""
    return swing {
        panel {
            borderLayout {
                left {
                    label("${item.name}:")
                }
                center {
                    textField(text = currentValue) {
                        attr {
                            addFocusListener(object : FocusListener {
                                override fun focusGained(e: FocusEvent?) {
                                }

                                override fun focusLost(e: FocusEvent?) {
                                    if (self.text.trim() != currentValue) {
                                        config.set(key, self.text.trim())
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}


private fun cfgPwdField(config: SourceConfigTran, key: String, item: SourceConfig.ConfigItem): JPanel {
    val currentValue = config.getNullable(key) ?: ""
    return swing {
        panel {
            borderLayout {
                left {
                    label("${item.name}:")
                }
                center {
                    passwordField(password = currentValue) {
                        attr {
                            addFocusListener(object : FocusListener {
                                override fun focusGained(e: FocusEvent?) {
                                }

                                override fun focusLost(e: FocusEvent?) {
                                    if (self.text.trim() != currentValue) {
                                        config.set(key, self.text.trim())
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
