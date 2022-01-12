package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel

class JSongTable(private val musicSource: MusicSource) : JScrollPane() {
    companion object {
        private val logger = JSongTable::class.logger
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    val tableComponent = JTable()

    class SongTableModel : AbstractTableModel() {
        private val data = arrayListOf<EntitySongInfo>()
        override fun getRowCount(): Int = data.size

        override fun getColumnName(column: Int): String =
            listOf("Cover", "Singer", "Name")[column]


        override fun getColumnCount(): Int = 3


        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            return when (columnIndex) {
                0 -> data[rowIndex].coverUrl
                1 -> data[rowIndex].singer
                2 -> data[rowIndex].title
                else -> error("Unknown rowIndex $rowIndex")
            }
        }

        fun add(element: EntitySongInfo) {
            data.add(element)
        }

        operator fun get(idx: Int) = data[idx]

        fun clear() {
            data.clear()
        }
    }

    val model = SongTableModel()

    private var sequence: Iterator<EntitySong> = iterator { }
    private var jobLoad: Job? = null
    private var isSequenceFin = true

    var onSongSelectedListener: (EntitySongInfo) -> Unit = {}

    init {
        setViewportView(tableComponent)
        verticalScrollBar.addAdjustmentListener {
            if (!it.valueIsAdjusting) {
                val offset = verticalScrollBar.visibleAmount
                val percentage = (it.value.toDouble() + offset) / verticalScrollBar.maximum
                if (percentage >= 0.9 && !isSequenceFin) {
                    fireLoadFromSequence()
                }
            }
        }
        tableComponent.model = model
        tableComponent.fillsViewportHeight = true
        tableComponent.selectionModel.apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            addListSelectionListener {
                if (it.valueIsAdjusting) return@addListSelectionListener
                onSongSelectedListener.invoke(model[it.firstIndex])
            }
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
                    song.forEach(model::add)
                    loaded++
                }
                if (!sequence.hasNext()) {
                    isSequenceFin = true
                }
                withContext(Dispatchers.Swing) {
                    model.fireTableDataChanged()
                }
            }
        }
    }

    fun setDataSource(data: Sequence<EntitySong>) {
        logger.info("data source changed")
        sequence = data.iterator()
        isSequenceFin = false
        model.clear()
        fireLoadFromSequence()
    }
}