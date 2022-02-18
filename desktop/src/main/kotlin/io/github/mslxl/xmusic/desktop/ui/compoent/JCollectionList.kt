package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.awt.Component
import javax.swing.*

class JCollectionList(val collectionProcessor: CollectionProcessor) : JScrollPane() {
    companion object {
        private val logger = JCollectionList::class.logger
    }

    val listComponent = JList<EntityCollection>()
    val listModel = DefaultListModel<EntityCollection>()

    val listCellRenderer = object : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            return if (value is EntityCollection) {
                super.getListCellRendererComponent(list, value.name, index, isSelected, cellHasFocus)
            } else {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            }
        }
    }


    private var isSequenceFin = true
    private var sequence = iterator<EntityCollectionIndex> { }
    private var jobLoad: Job? = null // Loading job from coroutine


    init {
        setViewportView(listComponent)
        listComponent.apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            model = listModel
            cellRenderer = listCellRenderer
        }

        // trigger when scrollbar value be set to 90%
        verticalScrollBar.addAdjustmentListener {
            if (!it.valueIsAdjusting) {
                val offset = verticalScrollBar.visibleAmount
                val percentage = (it.value.toDouble() + offset) / verticalScrollBar.maximum
                if (percentage >= 0.9 && !isSequenceFin) {
                    fireLoad()
                }
            }
        }

    }

    fun setData(sequence: Sequence<EntityCollectionIndex>) {
        logger.info("data source changed")
        this.sequence = sequence.iterator()
        isSequenceFin = false
        listComponent.clearSelection()
        listModel.clear()
        fireLoad()
    }

    /**
     * start load from [sequence]
     * it will create a coroutine and then work in IO-worker thread
     */
    private fun fireLoad() {
        if (jobLoad == null || jobLoad?.isActive == false) {
            logger.info("trigger load")
            jobLoad = GlobalScope.launch(Dispatchers.IO) {
                var loaded = 0
                val targetLoad = 20
                while (loaded < targetLoad && sequence.hasNext()) {
                    val elem = sequence.next()
                    val detail = collectionProcessor.getDetail(elem)
                    listModel.addElement(detail)
                    loaded++
                }
                if (!sequence.hasNext()) {
                    isSequenceFin = true
                }
            }
        }
    }

    fun addSelectionChangeListener(listener: (index: Int, EntityCollection?) -> Unit) {
        listComponent.selectionModel.addListSelectionListener {
            if (it.valueIsAdjusting) return@addListSelectionListener
            val entity = if (listComponent.selectedIndex >= 0) listModel.get(listComponent.selectedIndex) else null
            listener.invoke(listComponent.selectedIndex, entity)
        }
    }

}