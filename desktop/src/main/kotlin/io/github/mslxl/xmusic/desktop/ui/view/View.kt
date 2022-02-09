package io.github.mslxl.xmusic.desktop.ui.view

import io.github.mslxl.ktswing.CanAddChildrenScope
import javax.swing.JComponent

interface View {
    val parent: View?
    val root: JComponent

}

inline fun <reified T> View.findParent(): T? {
    var v = this.parent
    while (v != null) {
        if (v is T) {
            return v
        } else {
            v = v.parent
        }
    }
    return null
}

fun CanAddChildrenScope<*>.addView(view: View) {
    add(view.root)
}