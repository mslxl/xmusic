package io.github.mslxl.xmusic.desktop.ui.view

import io.github.mslxl.ktswing.CanAddChildrenScope
import javax.swing.JComponent

interface View {
    val root: JComponent
}

fun CanAddChildrenScope<*>.addView(view: View) {
    add(view.root)
}