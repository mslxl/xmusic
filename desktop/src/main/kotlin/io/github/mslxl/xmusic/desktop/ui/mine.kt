package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.list
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.component.splitPane
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.entity.EntityCollection
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.desktop.App
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import javax.swing.*
import javax.swing.table.AbstractTableModel

fun myFavPane(): JComponent {
    val srcSupportCollection = App.core.sourceList.map {
        App.core.getSrc(it)
    }.filter {
        it.collection != null
    }

    return swing {
        tabbedPane {
            tabPanel("My") {

            }
            srcSupportCollection.forEach { src ->
                tabPanelWith(src.name, borderLayoutCenter()) {
                    lazyPanel {
                        borderLayoutCenter {
                            splitPane {
                                allSplitPane {
                                    it.dividerSize = 2
                                }
                                val defModel = DefaultListModel<String>()

                                val leftList: JList<String>
                                val rightTable = JSongTable(src)
                                val collectionDataList = arrayListOf<EntityCollection>()
                                scrollPane {
                                    leftList = list<String>(defModel) {
                                        attr {
                                            selectionMode = ListSelectionModel.SINGLE_SELECTION
                                            selectionModel.addListSelectionListener { selectEvent ->
                                                if (selectEvent.valueIsAdjusting) return@addListSelectionListener
                                                // Show collection's content
                                                GlobalScope.launch(Dispatchers.IO) {
                                                    val entity = collectionDataList[selectEvent.firstIndex]
                                                    val seq = src.collection!!.getContent(entity)
                                                    rightTable.setDataSource(seq)
                                                }
                                            }
                                        }
                                    }
                                }
                                // Load all collection name
                                GlobalScope.launch(Dispatchers.Main) {
                                    src.collection!!.getAllCollection().forEach {
                                        //TODO lazy load
                                        val name = src.collection!!.getName(it)
                                        defModel.addElement(name)
                                        collectionDataList.add(it)
                                    }
                                }
                                add(rightTable)
                            }
                        }
                    }
                }
            }
        }
    }
}

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

        fun clear() {
            data.clear()
        }
    }

    val model = SongTableModel()

    private var sequence: Iterator<EntitySong> = iterator { }
    private var jobLoad: Job? = null
    private var isSequenceFin = true

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
