package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.BorderFactory
import javax.swing.JComboBox
import javax.swing.JComponent

class ProgramConfigView : View {
    private val controller = ProgramConfigController(this)
    lateinit var comboBoxFont: JComboBox<String>
    override val root: JComponent = swing {
        lazyPanelWith(borderLayoutCenter()) {
            scrollPane {
                vBox {
                    self.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
                    fontFamily()
                    hBox {
                        glue
                        button("Apply").addActionListener {
                            controller.commit()
                        }
                        glue
                    }
                }
            }
        }
    }

    private fun CanAddChildrenScope<*>.fontFamily() {
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
    }

    private fun JComponent.setCommonHeight(multi: Int = 1) {
        maximumSize = maximumSize.apply {
            height = 35 * multi
        }
    }
}