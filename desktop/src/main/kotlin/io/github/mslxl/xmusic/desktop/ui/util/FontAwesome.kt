package io.github.mslxl.xmusic.desktop.ui.util

import com.wordpress.tips4java.robcamick.TextIcon
import io.github.mslxl.ktswing.BasicScope
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import java.awt.Component
import java.awt.Font
import java.io.InputStream


object FontAwesome {
    private val logger = FontAwesome::class.logger
    private fun stream(ref: String): InputStream {
        logger.info("Read source: '$ref'")
        return App::class.java.getResourceAsStream(ref)!!
    }

    private fun InputStream.font(format: Int = Font.TRUETYPE_FONT): Font {
        return Font.createFont(format, this).deriveFont(Font.PLAIN, 26f)
    }

    val regularFont by lazy {
        stream("/io/github/mslxl/xmusic/desktop/res/Font Awesome 5 Free-Regular-400.otf").font()
    }

    val solidFont by lazy {
        stream("/io/github/mslxl/xmusic/desktop/res/Font Awesome 5 Free-Solid-900.otf").font()
    }
}

@Suppress("NOTHING_TO_INLINE", "unused")
inline fun <T : Component> BasicScope<T>.awesomeFontRegular() {
    self.font = FontAwesome.regularFont
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Component> BasicScope<T>.awesomeFontSolid() {
    self.font = FontAwesome.solidFont
}

@Suppress("NOTHING_TO_INLINE")
inline fun TextIcon.awesomeFontRegular() {
    font = FontAwesome.regularFont
}

@Suppress("NOTHING_TO_INLINE")
inline fun TextIcon.awesomeFontSolid() {
    font = FontAwesome.solidFont
}

