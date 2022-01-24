package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.component.passwordField
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.config.SourceConfigTran
import io.github.mslxl.xmusic.common.logger
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField

class PwdController : ConfigItemComponentController, FocusListener {
    companion object {
        private val logger = TextController::class.logger
    }

    private val label: JLabel
    private val textField: JPasswordField
    private val pan = swing<JPanel> {
        panelWith(borderLayout()) {
            left {
                label = label()
            }
            center {
                textField = passwordField()
            }
        }
    }
    private var config: SourceConfigTran? = null
    private var index: SourceConfig.ItemIndex? = null
    private val startEditListener = LinkedList<() -> Unit>()
    private val endEditListener = LinkedList<() -> Unit>()

    init {
        textField.addFocusListener(this)
    }

    override fun setData(trans: SourceConfigTran, itemIndex: SourceConfig.ItemIndex) {
        this.config = trans
        this.index = itemIndex
        label.text = "${itemIndex.name}:"
        textField.text = trans.getNullable(itemIndex.key) ?: ""
        pan.updateUI()
    }

    override fun addEditStartListener(listener: () -> Unit) {
        startEditListener.add(listener)
    }

    override fun addEditEndListener(listener: () -> Unit) {
        endEditListener.add(listener)
    }

    override fun getComponent(): JComponent {
        return pan
    }

    override fun focusGained(e: FocusEvent?) {
        logger.info("Start edit ${index?.key}(${index?.name})")
        startEditListener.forEach {
            it.invoke()
        }
    }

    override fun focusLost(e: FocusEvent?) {
        logger.info("End edit ${index?.key}(${index?.name})")
        index?.let { index -> config?.set(index.key, textField.text.trim()) }
        endEditListener.forEach {
            it.invoke()
        }
    }
}
