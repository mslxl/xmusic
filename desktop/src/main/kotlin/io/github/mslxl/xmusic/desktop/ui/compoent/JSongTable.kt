package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.ktswing.component.*
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.player.VirtualPlaylist
import io.github.mslxl.xmusic.common.source.MusicSource
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.AbstractTableModel

class JSongTable(private val musicSource: MusicSource, val playlist: VirtualPlaylist) : JScrollPane() {
    companion object {
        private val logger = JSongTable::class.logger
    }


    class SongTableModel : AbstractTableModel() {
        private val data = arrayListOf<EntitySongInfo>()
        val listData: List<EntitySongInfo> get() = data
        override fun getRowCount(): Int = data.size

        override fun getColumnName(column: Int): String =
            listOf("Cover", "Singer", "Name")[column]


        override fun getColumnCount(): Int = 3


        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
//            return when (columnIndex) {
//                0 -> data[rowIndex].coverUrl
//                1 -> data[rowIndex].singer
//                2 -> data[rowIndex].title
//                else -> error("Unknown rowIndex $rowIndex")
//            }
            return data[rowIndex]
        }

        fun add(element: EntitySongInfo) {
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

    private var sequence: Iterator<EntitySong> = iterator { } // Table will load src from this sequence
    private var jobLoad: Job? = null // Loading job from coroutine
    private var isSequenceFin = true // Is there anything remain in sequence

    init {
        setViewportView(tableComponent)
        // trigger load when scrollbar's value is 90%
        verticalScrollBar.addAdjustmentListener {
            if (!it.valueIsAdjusting) {
                val offset = verticalScrollBar.visibleAmount
                val percentage = (it.value.toDouble() + offset) / verticalScrollBar.maximum
                if (percentage >= 0.9 && !isSequenceFin) {
                    fireLoadFromSequence()
                }
            }
        }

        tableComponent.apply {
            fillsViewportHeight = true
            selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            rowHeight = 40
            model = tableModel
        }
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

        tableComponent.setDefaultRenderer(
            Any::class.java
        ) { _, value, isSelected, _, row, column ->
            val value = value as EntitySongInfo
            val comp = when (column) {
                0 -> swing<JComponent> {
                    button("Cover ${value.coverUrl}")
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


    private fun fireLoadFromSequence() {
        if (jobLoad == null || !jobLoad!!.isActive) {
            logger.info("trigger load")
            jobLoad = GlobalScope.launch(Dispatchers.IO) {
                var loaded = 0
                val targetLoad = 20
                while (loaded < targetLoad && sequence.hasNext()) {
                    val elem = sequence.next()
                    val song = musicSource.information.getInfo(elem)
                    song.forEach(tableModel::add)
                    loaded++
                }
                if (!sequence.hasNext()) {
                    isSequenceFin = true
                }
                withContext(Dispatchers.Swing) {
                    tableModel.fireTableDataChanged()
                }
            }
        }
    }

    fun setDataSource(data: Sequence<EntitySong>) {
        logger.info("data source changed")
        sequence = data.iterator()
        isSequenceFin = false
        tableComponent.clearSelection()
        tableModel.clear()
        fireLoadFromSequence()
    }
}
