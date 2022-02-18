package io.github.mslxl.xmusic.desktop.ui.compoent.config

import io.github.mslxl.ktswing.component.label
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.component.passwordField
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.xmusic.common.config.ConfigurationJournal
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
        private val logger = PwdController::class.logger
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
    private var journal: ConfigurationJournal? = null
    private var key: String? = null
    private val startEditListener = LinkedList<() -> Unit>()
    private val endEditListener = LinkedList<() -> Unit>()

    init {
        textField.addFocusListener(this)
    }

    override fun setData(journal: ConfigurationJournal, key: String) {
        this.journal = journal
        this.key = key
        label.text = key
        textField.text = journal.get(key)?.toString() ?: ""
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
        logger.info("Start edit $key")
        startEditListener.forEach {
            it.invoke()
        }
    }

    override fun focusLost(e: FocusEvent?) {
        logger.info("End edit $key)")
        key?.let { key ->
            //TODO encrypt this
            journal?.set(key, textField.text.trim())
        }
        endEditListener.forEach {
            it.invoke()
        }
    }
}
