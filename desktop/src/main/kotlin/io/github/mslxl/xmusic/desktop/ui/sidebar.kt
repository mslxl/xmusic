package io.github.mslxl.xmusic.desktop.ui

import com.wordpress.tips4java.robcamick.RotatedIcon
import com.wordpress.tips4java.robcamick.TextIcon
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.onAction
import java.awt.Insets
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JToolBar

fun sideBar(onDiscoveryAction: () -> Unit, onMineAction: () -> Unit, onSettingAction: () -> Unit): JComponent {
    return swing {
        add(JToolBar(JToolBar.VERTICAL).apply {
            CanAddChildrenScope(this).apply {
                attr {
                    isFloatable = false
                }
                val margin = Insets(20, 5, 20, 5)
                button("") {
                    attr {
                        icon = vertTextIcon("Discovery", this)
                        setMargin(margin)
                    }
                    onAction {
                        onDiscoveryAction.invoke()
                    }
                }
                button("") {
                    attr {
                        icon = vertTextIcon("My Fav", this)
                        setMargin(margin)
                    }
                    onAction {
                        onMineAction.invoke()
                    }
                }
                button("") {
                    attr {
                        icon = vertTextIcon("Settings", this)
                        setMargin(margin)
                    }
                    onAction {
                        onSettingAction.invoke()
                    }
                }
            }
        })
    }
}

private inline fun vertTextIcon(text: String, comp: JComponent): Icon {
    val icon = TextIcon(comp, text)
    val rotated = RotatedIcon(icon, RotatedIcon.Rotate.UP)
    return rotated
}
