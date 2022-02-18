package io.github.mslxl.xmusic.desktop.ui.util

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.config.DesktopConfig
import java.awt.Font
import javax.swing.UIManager
import javax.swing.plaf.FontUIResource

private const val internalFontPath = "/io/github/mslxl/xmusic/common/res/wqy-microhei.ttc"
fun initGlobalFont() {
    System.setProperty("awt.useSystemAAFontSettings", "on")
    System.setProperty("swing.aatext", "true")
    val font = if (DesktopConfig.font != "Internal") {
        Font(DesktopConfig.font, Font.PLAIN, DesktopConfig.fontSize)
    } else {
        Font.createFont(
                Font.TRUETYPE_FONT,
                XMusic::class.java.getResourceAsStream(internalFontPath)
        ).deriveFont(DesktopConfig.fontSize.toFloat())
    }
    val res = FontUIResource(font)
    logger("Util").info("Init global font as ${font.fontName}")
    UIManager.getDefaults()
            .keys()
            .iterator()
            .forEach {
                val value = UIManager.get(it)
                if (value is FontUIResource) {
                    UIManager.put(it, res)
                }
            }
}
