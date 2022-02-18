package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.hBox
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.config.IConfiguration
import io.github.mslxl.xmusic.common.config.UserConfiguration
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.ui.compoent.config.JConfigItemList
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.BorderFactory
import javax.swing.JComponent

class SourceConfigTabView(val config: IConfiguration, override val parent: View?) : View {
    constructor(userConfiguration: UserConfiguration, parent: View?) : this(userConfiguration.configuration, parent)

    companion object {
        val logger = SourceConfigTabView::class.logger
    }

    private val controller = SourceConfigTabController(this)
    val configComponentList = JConfigItemList(controller.trans)

    override val root: JComponent = swing {
        lazyPanel {
            logger.info("${config.id} config page created")
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
                            controller.commit()
                        }
                        struct(20)
                        button("Cancel").addActionListener {
                            controller.cancel()
                        }
                        glue
                    }
                }
            }
        }
    }
}