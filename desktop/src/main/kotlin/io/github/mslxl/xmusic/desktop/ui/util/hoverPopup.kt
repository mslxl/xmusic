package io.github.mslxl.xmusic.desktop.ui.util

import io.github.mslxl.ktswing.BasicScope
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.Popup
import javax.swing.PopupFactory
import javax.swing.Timer

private class MouseHoverController(
    val hoverComp: JComponent, val showPanel: JComponent, val position: PopupPosition, timeout: Int
) : MouseAdapter() {
    val popupFactory: PopupFactory = PopupFactory.getSharedInstance()
    var popup: Popup? = null
    val timer = Timer(timeout) {
        popup?.hide()
        popup = null
    }.apply {
        isRepeats = false
    }

    init {
        fun addMouseListenerForAllChildrenComponents(comp: JComponent) {
            comp.components.forEach {
                it.addMouseListener(this)
                if (it is JComponent) {
                    addMouseListenerForAllChildrenComponents(it)
                }
            }
        }
        showPanel.addMouseListener(this)
        hoverComp.addMouseListener(this)
        addMouseListenerForAllChildrenComponents(showPanel)
        addMouseListenerForAllChildrenComponents(hoverComp)

    }

    fun makeShow() {
        // if popup is showing, then prevent it from hiding, or create and show it
        if (popup == null) {
            val point = when (position) {
                is PopupPosition.UpOwner -> {
                    val hoverCompLocation = hoverComp.locationOnScreen
                    val y = hoverCompLocation.y - showPanel.preferredSize.height + position.offsetY
                    val hoverCompCenterYAxisOffset = hoverComp.width / 2
                    val popupCenterYAxisOffset = showPanel.preferredSize.width / 2
                    val x =
                        hoverCompLocation.x + (hoverCompCenterYAxisOffset - popupCenterYAxisOffset) + position.offsetX
                    Point(x, y)
                }
                is PopupPosition.DownOwner -> {
                    val hoverCompLocation = hoverComp.locationOnScreen
                    val y = hoverCompLocation.y + hoverComp.height + position.offsetY
                    val hoverCompCenterYAxisOffset = hoverComp.width / 2
                    val popupCenterYAxisOffset = showPanel.preferredSize.width / 2
                    val x =
                        hoverCompLocation.x + (hoverCompCenterYAxisOffset - popupCenterYAxisOffset) + position.offsetX
                    Point(x, y)
                }
                is PopupPosition.DynamicCalc ->
                    position.calc.invoke(hoverComp, showPanel)
            }
            popup = popupFactory.getPopup(hoverComp, showPanel, point.x, point.y).apply {
                show()
            }

        } else {
            if (timer.isRunning) {
                timer.stop()
            }
        }
    }

    override fun mouseExited(e: MouseEvent?) {
        timer.restart()
    }

    override fun mouseEntered(e: MouseEvent?) {
        makeShow()
    }

    override fun mouseMoved(e: MouseEvent?) {
        if (e?.source == showPanel) {
            makeShow()
        }
    }
}

sealed class PopupPosition {
    class UpOwner(val offsetX: Int = 0, val offsetY: Int = 0) : PopupPosition()
    class DownOwner(val offsetX: Int = 0, val offsetY: Int = 0) : PopupPosition()
    class DynamicCalc(val calc: (owner: JComponent, popup: JComponent) -> Point) : PopupPosition()
}

fun linkHoverPopup(hoverComp: JComponent, showPanel: JComponent, position: PopupPosition, timeout: Int = 200) {
    MouseHoverController(hoverComp, showPanel, position, timeout)
}

fun <T : JComponent> BasicScope<T>.linkHoverPopup(showPanel: JComponent, position: PopupPosition, timeout: Int = 200) {
    linkHoverPopup(this.self, showPanel, position, timeout)
}