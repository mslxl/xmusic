package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.list
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.source.processor.ExplorableIndex
import io.github.mslxl.xmusic.common.source.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.util.MusicUtils
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.util.scale
import io.github.mslxl.xmusic.desktop.ui.view.View
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class DiscoveryColumView<T : ExplorableIndex<E>, E : ExplorableEntity>(
    val i18nKey: I18NKey,
    val processor: ExplorerProcessor<T, E>,
    val musicSource: MusicSource,
    override val parent: View
) :
    View {
    val scrollPane: JScrollPane
    val list: JList<ExplorableEntity>

    private val renderer = object : ListCellRenderer<ExplorableEntity> {
        val label = JLabel().apply {
            border = BorderFactory.createEmptyBorder(5, 5, 15, 5)
        }

        override fun getListCellRendererComponent(
            list: JList<out ExplorableEntity>?,
            value: ExplorableEntity?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val img = value?.cover?.let { url ->
                App.core.network.download(musicSource, url, true)
            } ?: MusicUtils.defaultCover
            label.icon = ImageIcon(img.toURI().toURL()).scale(128, 128)
            return label
        }
    }


    override val root: JComponent = swing {
        panel {
            attr {
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createTitledBorder(i18nKey)
                )
            }
            borderLayoutCenter {
                scrollPane = scrollPane {
                    attr {
                        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                    }
                    list = list {
                        attr {
                            layoutOrientation = JList.HORIZONTAL_WRAP
                            visibleRowCount = 1
                            selectionMode = ListSelectionModel.SINGLE_SELECTION
                            cellRenderer = renderer
                        }
                        self.addMouseListener(object : MouseAdapter() {
                            override fun mouseClicked(e: MouseEvent?) {
                                controller.openDetail(self.selectedValue)
                            }
                        })
                    }
                }
            }
        }
    }
    private val controller = DiscoveryColumController(this).apply {
        fireLoad()
    }
}