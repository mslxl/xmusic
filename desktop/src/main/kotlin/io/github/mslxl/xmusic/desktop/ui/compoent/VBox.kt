package io.github.mslxl.xmusic.desktop.ui.compoent

import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.component.BoxScope
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.Scrollable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class VBox : Box(BoxLayout.Y_AXIS), Scrollable {
    override fun getPreferredScrollableViewportSize(): Dimension {
        return size
    }

    override fun getScrollableUnitIncrement(visibleRect: Rectangle?, orientation: Int, direction: Int): Int {
        return 10
    }

    override fun getScrollableBlockIncrement(visibleRect: Rectangle?, orientation: Int, direction: Int): Int {
        return 10
    }

    override fun getScrollableTracksViewportWidth(): Boolean {
        return true
    }

    override fun getScrollableTracksViewportHeight(): Boolean {
        return false
    }
}

@OptIn(ExperimentalContracts::class)
inline fun CanAddChildrenScope<*>.fvBox(block: BoxScope.() -> Unit): Box {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val box = VBox()
    BoxScope(box, true).apply(block)
    add(box)
    return box
}
