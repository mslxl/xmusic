package io.github.mslxl.xmusic.desktop.ui.view.root

import com.wordpress.tips4java.robcamick.CompoundIcon
import com.wordpress.tips4java.robcamick.RotatedIcon
import com.wordpress.tips4java.robcamick.TextIcon
import io.github.mslxl.ktswing.CanAddChildrenScope
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.button
import io.github.mslxl.ktswing.component.toolBar
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.onAction
import io.github.mslxl.xmusic.desktop.ui.util.awesomeFontSolid
import io.github.mslxl.xmusic.desktop.ui.view.View
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JToolBar

class SideBarView : View {
    private val toolbar: JToolBar
    override val root: JComponent =
        swing {
            toolbar = toolBar(orient = JToolBar.VERTICAL) {
                attr {
                    isFloatable = false
                }
            }
        }

    fun addAction(iconText: String, text: String, action: () -> Unit) {
        with(CanAddChildrenScope(toolbar)) {
            button("") {
                attr {
                    icon = vertCompoundIcon(iconText, text, self)
                }
                onAction {
                    action.invoke()
                }
            }
            self.addSeparator()
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
}
