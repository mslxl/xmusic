package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.slider
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.gridBagLayout
import javax.swing.JPanel

fun playBar(): JPanel {
    return swing {
        panel {
            gridBagLayout {
                cell {
                    cons {
                        gridx = 3
                    }
                    button("IMG")
                }
                cell {
                    cons {
                        gridx = 4
                        gridwidth = 4
                    }
                    slider()
                }
                cell {
                    cons {
                        gridx = 9
                    }
                    button("Pre")
                }
                cell {
                    cons {
                        gridx = 10
                    }
                    button("Ply")
                }

                cell {
                    cons {
                        gridx = 11
                    }
                    button("Nxt")
                }

                cell {
                    cons {
                        gridx = 13
                    }
                    button("Aud")
                }
                cell {
                    cons {
                        gridx = 14
                    }
                    button("Meta")
                }

            }
        }
    }
}