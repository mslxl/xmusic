package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.hBox
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JConfigItemList
import io.github.mslxl.xmusic.desktop.ui.subpage.aboutPage
import javax.swing.BorderFactory
import javax.swing.JTabbedPane

fun settingsPane(): JTabbedPane {
    val logger = logger("Settings Pane")
    val src = App.core.sourceList.map { App.core.getCfg(it) }.filter {
        it.listAllMarks().isNotEmpty()
    }

    return swing {
        tabbedPane {
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
                                    glue
                                    button("Apply").addActionListener {
                                        logger.info("$srcName config commit")
                                        trans.commit()
                                    }
                                    glue
                                    button("Cancel").addActionListener {
                                        logger.info("Trash modified config")
                                        TODO()
                                    }
                                    glue
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

