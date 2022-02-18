package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.component.adv.lazyPanelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.XMusicConfiguration
import io.github.mslxl.xmusic.desktop.config.DesktopConfig
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.*

class ProgramConfigView(override val parent: View?) : View {
    private val controller = ProgramConfigController(this)

    val comboBoxFont: JComboBox<String>
    val spinnerFontSize: JSpinner
    val textFieldLang: JTextField


    override val root: JComponent = swing {
        lazyPanelWith(borderLayoutCenter()) {
            scrollPane {
                vBox {
                    self.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)

                    textFieldLang = lang()
                    comboBoxFont = fontFamily()
                    spinnerFontSize = fontSize()

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

    private fun CanAddChildrenScope<*>.lang(): JTextField {
        val textField: JTextField
        panel {
            self.setCommonHeight()
            borderLayout {
                left {
                    label("Language:")
                }
                center {
                    textField = textField(text = XMusicConfiguration.Companion.language)
                }
            }
        }
        return textField

    }

    private fun CanAddChildrenScope<*>.fontSize(): JSpinner {
        val spinner: JSpinner
        panel {
            self.setCommonHeight()
            borderLayout {
                left {
                    label("Font size:")
                }
                center {
                    spinner = spinner(
                            SpinnerNumberModel(
                                    DesktopConfig.fontSize,
                                    1, 100,
                                    1
                            )
                    )
                }
            }
        }
        return spinner

    }

    private fun CanAddChildrenScope<*>.fontFamily(): JComboBox<String> {
        val comboBox: JComboBox<String>
        panel {
            self.setCommonHeight()
            borderLayout(10) {
                left {
                    label("Font")
                }
                center {
                    comboBox = comboBox(controller.availableFont) {
                        val curFont = DesktopConfig.font
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
        return comboBox
    }

    private fun JComponent.setCommonHeight(multi: Int = 1) {
        maximumSize = maximumSize.apply {
            height = 35 * multi
        }
    }
}