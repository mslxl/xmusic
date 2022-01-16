package io.github.mslxl.xmusic.desktop.ui.util

import io.github.mslxl.ktswing.BasicScope
import java.awt.Point
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.Popup
import javax.swing.PopupFactory

private class TogglePopupController(
    val triggerComp: JComponent,
    val showComp: JComponent,
    val position: PopupPosition
) : MouseAdapter(), FocusListener {
    val popupFactory: PopupFactory = PopupFactory.getSharedInstance()
    var popup: Popup? = null

    init {
        triggerComp.addMouseListener(this)
        showComp.addFocusListener(this)
        showComp.grabFocus()
    }

    fun show() {
        val point = when (position) {
            is PopupPosition.UpOwner -> {
                val hoverCompLocation = triggerComp.locationOnScreen
                val y = hoverCompLocation.y - triggerComp.preferredSize.height + position.offsetY
                val hoverCompCenterYAxisOffset = triggerComp.width / 2
                val popupCenterYAxisOffset = showComp.preferredSize.width / 2
                val x =
                    hoverCompLocation.x + (hoverCompCenterYAxisOffset - popupCenterYAxisOffset) + position.offsetX
                Point(x, y)
            }
            is PopupPosition.DownOwner -> {
                val triggerCompLoc = triggerComp.locationOnScreen
                val y = triggerCompLoc.y + triggerComp.height + position.offsetY
                val triggerCenterYAxisOffset = triggerComp.width / 2
                val popupCenterYAxisOffset = showComp.preferredSize.width / 2
                val x =
                    triggerCompLoc.x + (triggerCenterYAxisOffset - popupCenterYAxisOffset) + position.offsetX
                Point(x, y)
            }
            is PopupPosition.DynamicCalc ->
                position.calc.invoke(triggerComp, showComp)
        }
        popup = popupFactory.getPopup(triggerComp, showComp, point.x, point.y).apply {
            show()
        }
    }

    fun hide() {
        popup?.hide()
        popup = null
    }

    fun toggle() {
        if (popup == null) {
            show()
        } else {
            hide()
        }
    }

    override fun mouseClicked(e: MouseEvent?) {
        toggle()
    }


    override fun focusGained(e: FocusEvent?) {
    }

    override fun focusLost(e: FocusEvent?) {
        hide()
    }
}

fun linkTogglePopup(btn: JComponent, showPanel: JComponent, position: PopupPosition) {
    TogglePopupController(btn, showPanel, position)
}

fun <T : JComponent> BasicScope<T>.linkTogglePopup(showPanel: JComponent, position: PopupPosition) {
    linkTogglePopup(this.self, showPanel, position)
}
