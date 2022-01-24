package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.passwordField
import io.github.mslxl.ktswing.component.textField
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import io.github.mslxl.xmusic.common.logger
import java.awt.Component
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.event.CellEditorListener
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class JConfigItemList(private val trans: SourceConfigTran) : JTable() {
    companion object {
        private val logger = JConfigItemList::class.logger
    }

    private var marks = trans.listAllMarks().map { it.value }.toList()
    private val itemModel = object : AbstractTableModel() {
        override fun getRowCount(): Int = marks.size

        override fun getColumnCount(): Int = 1

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = marks[rowIndex]
        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    }

    private val renderer = TableCellRenderer { table, value, isSelected, hasFocus, row, column ->
        val value = value as SourceConfig.ConfigItem
        //TODO reuse component to reduce resource
        buildSourceConfigItemComponent(trans, value)
    }
    private val editor = object : TableCellEditor {

        override fun getCellEditorValue(): Any {
            TODO("Not yet implemented")
        }

        override fun isCellEditable(anEvent: EventObject?): Boolean = true


        override fun shouldSelectCell(anEvent: EventObject?): Boolean = false


        override fun stopCellEditing(): Boolean = true


        override fun cancelCellEditing() {
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
            val value = value as SourceConfig.ConfigItem
            return buildSourceConfigItemComponent(trans, value)
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
}

private fun buildSourceConfigItemComponent(trans: SourceConfigTran, configItem: SourceConfig.ConfigItem): JComponent {
    return when (configItem.type) {
        SourceConfig.ItemType.TEXT -> cfgTextField(
            trans,
            configItem.key,
            configItem
        )
        SourceConfig.ItemType.PASSWORD -> cfgPwdField(
            trans,
            configItem.key,
            configItem
        )
        else -> error("Unknown config type ${configItem.type}")
    }
}


private fun cfgTextField(config: SourceConfigTran, key: String, item: SourceConfig.ConfigItem): JComponent {
    val currentValue = config.getNullable(key) ?: ""
    return swing {
        panel {
            borderLayout {
                left {
                    label("${item.name}:")
                }
                center {
                    textField(text = currentValue) {
                        attr {
                            addFocusListener(object : FocusListener {
                                override fun focusGained(e: FocusEvent?) {
                                }

                                override fun focusLost(e: FocusEvent?) {
                                    if (self.text.trim() != currentValue) {
                                        config.set(key, self.text.trim())
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}


private fun cfgPwdField(config: SourceConfigTran, key: String, item: SourceConfig.ConfigItem): JComponent {
    val currentValue = config.getNullable(key) ?: ""
    return swing {
        panel {
            borderLayout {
                left {
                    label("${item.name}:")
                }
                center {
                    passwordField(password = currentValue) {
                        attr {
                            addFocusListener(object : FocusListener {
                                override fun focusGained(e: FocusEvent?) {
                                }

                                override fun focusLost(e: FocusEvent?) {
                                    if (self.text.trim() != currentValue) {
                                        config.set(key, self.text.trim())
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
