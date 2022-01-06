package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.flowLayout
import io.github.mslxl.xmusic.desktop.App
import javax.swing.JTabbedPane
import javax.swing.border.TitledBorder

fun discoveryPane(): JTabbedPane {
    val srcSupported = App.core.sourceList.map {
        App.core.getSrc(it)
    }.filter {
        it.discovery != null
    }
    return swing {
        tabbedPane {
            srcSupported.forEach { src ->
                tab(src.name) {
                    //TODO Switch to VBOX layout
                    flowLayout {
                        src.discovery!!.keys.forEach { discoveryId ->
                            val discovery = src.discovery!![discoveryId]!!
                            panel {
                                attr {
                                    border = TitledBorder(discoveryId)
                                }
                                flowLayout {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

