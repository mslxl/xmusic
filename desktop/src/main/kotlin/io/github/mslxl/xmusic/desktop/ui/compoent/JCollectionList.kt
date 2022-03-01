package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.util.ChannelListAppender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
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
    var channelListAppender: ChannelListAppender<EntityCollectionIndex>? = null

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
                if (percentage >= 0.9) {
                    fireLoad()
                }
            }
        }

    }

    fun fireLoad() {
        channelListAppender?.load(10)
    }

    private suspend fun appendEntityCollectionFromIndex(index: EntityCollectionIndex) {
        withContext(Dispatchers.IO) {
            val collection = collectionProcessor.getDetail(index)
            withContext(Dispatchers.Swing) {
                listModel.addElement(collection)
            }
        }
    }

    fun setData(channel: ReceiveChannel<EntityCollectionIndex>) {
        logger.info("data source changed")
        channelListAppender = ChannelListAppender(this::appendEntityCollectionFromIndex, channel, Dispatchers.IO)
        listComponent.clearSelection()
        listModel.clear()
        fireLoad()
    }


    fun addSelectionChangeListener(listener: (index: Int, EntityCollection?) -> Unit) {
        listComponent.selectionModel.addListSelectionListener {
            if (it.valueIsAdjusting) return@addListSelectionListener
            val entity = if (listComponent.selectedIndex >= 0) listModel.get(listComponent.selectedIndex) else null
            listener.invoke(listComponent.selectedIndex, entity)
        }
    }

}