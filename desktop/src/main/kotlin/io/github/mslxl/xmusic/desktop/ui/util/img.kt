package io.github.mslxl.xmusic.desktop.ui.util

import java.awt.Image
import javax.swing.ImageIcon

inline fun ImageIcon.scale(width: Int, height: Int = width): ImageIcon {
    return apply {
        image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT)
    }
}