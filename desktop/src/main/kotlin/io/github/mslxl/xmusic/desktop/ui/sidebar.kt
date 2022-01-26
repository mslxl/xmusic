package io.github.mslxl.xmusic.desktop.ui

import com.wordpress.tips4java.robcamick.CompoundIcon
import com.wordpress.tips4java.robcamick.RotatedIcon
import com.wordpress.tips4java.robcamick.TextIcon
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.toolBar
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.onAction
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JToolBar

fun sideBar(onDiscoveryAction: () -> Unit, onMineAction: () -> Unit, onSettingAction: () -> Unit): JComponent {
    return swing {
        toolBar(orient = JToolBar.VERTICAL) {
            attr {
                isFloatable = false
            }
            button("") {
                attr {
                    icon = vertCompoundIcon("\uf002", "Discovery", this)
                }
                onAction {
                    onDiscoveryAction.invoke()
                }
            }
            self.addSeparator()
            button("") {
                attr {
                    icon = vertCompoundIcon("\uf004", "Collection", this)
                }
                onAction {
                    onMineAction.invoke()
                }
            }
            self.addSeparator()
            button("") {
                attr {
                    icon = vertCompoundIcon("\uf4fe", "Settings", this)
                }
                onAction {
                    onSettingAction.invoke()
                }
            }
        }
    }
}


@Suppress("NOTHING_TO_INLINE")
private inline fun vertCompoundIcon(iconText: String, text: String, comp: JComponent): Icon {
    val tt = TextIcon(comp, text)
    val icon = TextIcon(comp, iconText).apply {
        awesomeFontSolid()
    }
    val compoundIcon = CompoundIcon(CompoundIcon.Axis.X_AXIS, 4, icon, tt)

    return RotatedIcon(compoundIcon, RotatedIcon.Rotate.UP)
}
