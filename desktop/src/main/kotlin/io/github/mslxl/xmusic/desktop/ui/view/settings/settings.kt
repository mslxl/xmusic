package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.config.JConfigItemList
import io.github.mslxl.xmusic.desktop.ui.subpage.aboutPage
import javax.swing.*

private val logger = logger("Settings Pane")

fun settingsPane(): JTabbedPane {
    val src = App.core.sourceList.map { App.core.getCfg(it) }.filter {
        it.listAllMarks().isNotEmpty()
    }

    return swing {
        tabbedPane {
            tabPanelWith("XMusic", borderLayoutCenter()) {
                add(buildProgramConfig())
            }
            applySourceConfigPage(src, this)
            tabPanelWith("About", borderLayoutCenter()) {
                add(aboutPage())
            }
        }
    }
}

fun buildProgramConfig(): JComponent = swing {
    fun JComponent.setCommonHeight(multi: Int = 1) {
        maximumSize = maximumSize.apply {
            height = 35 * multi
        }
    }

    val comboBoxFont: JComboBox<String>
    lazyPanelWith(borderLayoutCenter()) {
        scrollPane {
            vBox {
                self.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
                panel {
                    self.setCommonHeight()
                    borderLayout(10) {
                        left {
                            label("Font")
                        }
                        center {
                            comboBoxFont = comboBox(App.core.programConfig.availableFont) {
                                val curFont = App.core.programConfig.font
                                for (i in 0..self.model.size) {
                                    if (self.model.getElementAt(i) == curFont) {
                                        self.selectedIndex = i
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                hBox {
                    glue
                    button("Apply").addActionListener {
                        with(App.core.programConfig) {
                            var needRestart = false
                            if (font != comboBoxFont.selectedItem) {
                                font = comboBoxFont.selectedItem?.toString() ?: defaultFont.first
                                needRestart = true
                            }
                            save()
                            if (needRestart) {
                                JOptionPane.showMessageDialog(self, "Some settings need restart to take effect")
                            }
                        }
                    }
                    glue
                }
            }
        }
    }
}


fun applySourceConfigPage(src: List<SourceConfig>, tabbedPaneScope: TabbedPaneScope) {
    with(tabbedPaneScope) {
        src.forEach { sourceConfig ->
            val srcName = App.core.getSrc(sourceConfig.id).name
            tabPanelWith(srcName, borderLayoutCenter()) {
                lazyPanel {
                    var trans = sourceConfig.transact()
                    val configComponentList = JConfigItemList(trans)
                    logger.info("$srcName config page created")
                    borderLayout {
                        center {
                            scrollPane {
                                attr {
                                    border = BorderFactory.createEmptyBorder(0, 15, 5, 15)
                                }
                                add(configComponentList)
                            }
                        }
                        bottom {
                            hBox {
                                attr {
                                    border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
                                }
                                glue
                                button("Apply").addActionListener {
                                    logger.info("$srcName config commit")
                                    trans.commit()
                                }
                                struct(20)
                                button("Cancel").addActionListener {
                                    logger.info("Trash modified config")
                                    trans = sourceConfig.transact()
                                    configComponentList.reload(trans)
                                }
                                glue
                            }
                        }
                    }
                }
            }
        }
    }
}

