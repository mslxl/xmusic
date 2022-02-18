package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.xmusic.common.config.ConfigurationJournal
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.ui.compoent.config.ControllerBuilder
import java.awt.Component
import java.util.*
import javax.swing.JTable
import javax.swing.event.CellEditorListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class JConfigItemList(private var journal: ConfigurationJournal) : JTable() {
    companion object {
        private val logger = JConfigItemList::class.logger
    }

    private val controllerBuilder = ControllerBuilder()
    private lateinit var itemModel: DefaultTableModel

    init {
        reload(journal)
    }


    private val renderer = TableCellRenderer { _, value, _, _, _, _ ->
        val key = value as String
        controllerBuilder.getController(journal, key).getComponent()
    }
    private val editor = object : TableCellEditor {

        override fun getCellEditorValue(): Any {
            error("Unsupported operation")
        }

        override fun isCellEditable(anEvent: EventObject?): Boolean = true


        override fun shouldSelectCell(anEvent: EventObject?): Boolean = false


        override fun stopCellEditing(): Boolean = true


        override fun cancelCellEditing() {
            clearSelection()
        }

        override fun addCellEditorListener(l: CellEditorListener?) {
        }

        override fun removeCellEditorListener(l: CellEditorListener?) {
        }

        override fun getTableCellEditorComponent(
                table: JTable?,
                value: Any?,
                isSelected: Boolean,
                row: Int,
                column: Int
        ): Component {
            val key = value as String
            return controllerBuilder.getController(journal, key)
                    .getComponent()
        }
    }

    init {
        tableHeader.isVisible = false
        this.model = itemModel
        this.setDefaultRenderer(Any::class.java, renderer)
        this.setDefaultEditor(Any::class.java, editor)
        rowHeight += 20
        this.setCellEditor(editor)
    }

    fun reload(journal: ConfigurationJournal) {
        logger.info("Reload config")
        this.journal = journal
        itemModel = DefaultTableModel().apply {
            addColumn("")
        }

        journal.exposedKey.keys.forEach {
            itemModel.addRow(arrayOf(it))
        }


        this.model = itemModel
    }
}


