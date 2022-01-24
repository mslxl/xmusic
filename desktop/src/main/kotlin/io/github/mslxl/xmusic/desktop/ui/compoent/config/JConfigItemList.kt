package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import io.github.mslxl.xmusic.common.logger
import java.awt.Component
import java.util.*
import javax.swing.JTable
import javax.swing.event.CellEditorListener
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class JConfigItemList(private var trans: SourceConfigTran) : JTable() {
    companion object {
        private val logger = JConfigItemList::class.logger
    }

    private val controllerBuilder = ControllerBuilder()
    private lateinit var itemModel: DefaultTableModel

    init {
        reload(trans)
    }


    private val renderer = TableCellRenderer { _, value, _, _, _, _ ->
        val value = value as SourceConfig.ItemIndex
        controllerBuilder.getController(trans, value).getComponent()
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
            val value = value as SourceConfig.ItemIndex
            return controllerBuilder.getController(trans, value)
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

    fun reload(trans: SourceConfigTran) {
        logger.info("Reload config")
        this.trans = trans
        itemModel = DefaultTableModel().apply {
            addColumn("")
        }
        trans.listAllMarks().forEach {
            itemModel.addRow(arrayOf(it.value))
        }
        this.model = itemModel
    }
}


