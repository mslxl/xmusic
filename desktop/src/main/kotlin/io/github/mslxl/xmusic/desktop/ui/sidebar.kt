package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.gridLayout
import io.github.mslxl.ktswing.onAction
import javax.swing.JPanel

fun sideBar(onDiscoveryAction: () -> Unit, onMineAction: () -> Unit, onSettingAction: () -> Unit): JPanel {
    return swing {
        panel {
            //TODO Switch to VBOX layout
            gridLayout {
                row {
                    button("Discovery") {
                        onAction {
                            onDiscoveryAction.invoke()
                        }
                    }
                }

                row {
                    button("My") {
                        onAction {
                            onMineAction.invoke()
                        }
                    }
                }

                row {
                    button("Settings") {
                        onAction {
                            onSettingAction.invoke()
                        }
                    }
                }
            }
        }
    }
}