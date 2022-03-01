package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.player.VirtualPlaylist
import io.github.mslxl.xmusic.common.util.ChannelListAppender
import io.github.mslxl.xmusic.common.util.MusicUtils
import io.github.mslxl.xmusic.desktop.ui.util.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.AbstractTableModel

class JSongTable(private val musicSource: MusicSource, val playlist: VirtualPlaylist) : JScrollPane() {
    companion object {
        private val logger = JSongTable::class.logger
    }


    class SongTableModel : AbstractTableModel() {
        private val data = arrayListOf<EntitySong>()
        val listData: List<EntitySong> get() = data
        override fun getRowCount(): Int = data.size

        override fun getColumnName(column: Int): String =
            listOf("Cover", "Name", "Singer")[column]


        override fun getColumnCount(): Int = 3


        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            return data[rowIndex]
        }

        fun add(element: EntitySong) {
            data.add(element)
        }

        operator fun get(idx: Int) = if (idx > data.lastIndex || idx < 0) null else data[idx]

        fun clear() {
            data.clear()
            fireTableDataChanged()
        }


    }

    val tableComponent = JTable()
    val tableModel = SongTableModel()
    var pauseLoad = false

    var channelListAppender: ChannelListAppender<EntitySongIndex>? = null


    init {
        setViewportView(tableComponent)
        // trigger load when scrollbar's value is 90%
        verticalScrollBar.addAdjustmentListener {
            if (!it.valueIsAdjusting) {
                val offset = verticalScrollBar.visibleAmount
                val percentage = (it.value.toDouble() + offset) / verticalScrollBar.maximum
                if (percentage >= 0.9) {
                    logger.info("scrollbar approach bottom(percentage $percentage), trigger load")
                    loadMore()
                }
            }
        }

        tableComponent.apply {
            fillsViewportHeight = true
            selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            rowHeight = 40
            model = tableModel
        }
        // right click cell to show popup menu
        tableComponent.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (tableComponent.selectedRow >= 0) {
                    if (e.clickCount == 2 && e.button == MouseEvent.BUTTON1) {
                        // replace playlist with current list,and then play clicked music
                        playlist.replace(tableModel.listData)
                        playlist.currentPos = tableComponent.selectedRow
                        playlist.notifyCurrentChange()
                    } else if (e.button == MouseEvent.BUTTON3) {
                        val songs = tableComponent.selectedRows.map { tableModel[it] }.filterNotNull()
                        // show PopupMenu
                        tableComponent.showPopupMenu(e.x, e.y) {
                            item("Play") {
                                onAction {
                                    val needToSwitch = playlist.currentPos != -1
                                    playlist.addAfterCurrent(songs)
                                    if (needToSwitch) {
                                        playlist.currentPos++
                                    }
                                    playlist.notifyCurrentChange()
                                }
                            }
                            item("Play next") {
                                onAction {
                                    playlist.addAfterCurrent(songs)
                                }
                            }
                            separator
                            item("Edit metadata")

                        }
                    }
                }
            }
        })

        // set width of table column
        tableComponent.columnModel.apply {
            getColumn(0).apply {
                preferredWidth = tableComponent.rowHeight
                resizable = false
            }
            getColumn(1).apply {
                preferredWidth = 120
            }
            getColumn(2).apply {
                preferredWidth = 200
            }
        }

        // set default renderer to show different field
        tableComponent.setDefaultRenderer(
            Any::class.java
        ) { _, value, isSelected, _, row, column ->
            val value = value as EntitySong
            val comp = when (column) {
                0 -> swing {
                    hBox {
                        glue
                        imageLabel {
                            attr {
                                val size = tableComponent.rowHeight
                                preferredSize = Dimension(size, size)
                                self.icon =
                                    ImageIcon(
                                        value.cover
                                            ?: MusicUtils.defaultCover.toURI().toURL()
                                    ).scale(size, size)
                            }
                        }
                        glue
                    }
                }
                1 -> swing<JComponent> {
                    label(value.title)
                }
                2 -> swing {
                    label(value.singer)
                }
                else -> error("Invalid column index in renderer")
            }
            if (isSelected) {
                comp.border = BorderFactory.createLoweredBevelBorder()
            }
            comp
        }

    }

    fun loadMore() {
        if (pauseLoad) {
            logger.info("load was paused by last task")
        } else {
            pauseLoad = true
            channelListAppender?.load(10)
        }
    }

    private suspend fun appendEntitySongFromIndex(index: EntitySongIndex) {
        withContext(Dispatchers.IO) {
            val source = AddonsMan.getInstance<MusicSource>(index.source)!!
            val songs = source.information.getDetail(index)
            withContext(Dispatchers.Swing) {
                songs.forEach(tableModel::add)
                tableModel.fireTableDataChanged()
            }
        }
    }

    fun setDataSource(data: ReceiveChannel<EntitySongIndex>) {
        pauseLoad = false
        logger.info("data source changed")
        channelListAppender = ChannelListAppender(this::appendEntitySongFromIndex, data, Dispatchers.IO)
        channelListAppender!!.addLoadSuccessListener {
            withContext(Dispatchers.Swing) {
                pauseLoad = false
//                tableModel.fireTableDataChanged()
                // to make sure the scroll bar appear in time, the table must be updated first
                if (!verticalScrollBar.isVisible) {
                    logger.info("continue load more to fill the page")
                    loadMore()
                }
            }
        }
        tableComponent.clearSelection()
        tableModel.clear()
        loadMore()
    }
}
