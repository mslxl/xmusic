package io.github.mslxl.xmusic.desktop

import com.formdev.flatlaf.FlatDarculaLaf
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.exitOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.resizable
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.i18n.I18N
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.src.SourceLocalMusic
import io.github.mslxl.xmusic.desktop.config.DesktopConfig
import io.github.mslxl.xmusic.desktop.fs.CacheIndexDBImpl
import io.github.mslxl.xmusic.desktop.fs.LocalFile
import io.github.mslxl.xmusic.desktop.i18n.desktopI18nRes
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import io.github.mslxl.xmusic.desktop.ui.util.initGlobalFont
import io.github.mslxl.xmusic.desktop.ui.view.root.RootView
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.UIManager

object App : I18N {
    const val version = "0.0.1-alpha"
    private val logger = App::class.logger
    override val id: String = XMusic.appID
    override val i18n = desktopI18nRes
    private val fs = LocalFile()
    val desktopConfig = DesktopConfig(fs)
    val core: XMusic = XMusic(fs, VlcjControl, CacheIndexDBImpl()).apply {
        addMusicSource(SourceLocalMusic(this))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("XMusic desktop($version) start")
        try {
            initGlobalFont()
            JFrame.setDefaultLookAndFeelDecorated(true)
            JDialog.setDefaultLookAndFeelDecorated(true)
            FlatDarculaLaf.setup()
        } catch (e: ExceptionInInitializerError) {
            logger.warn("Fail to use darcula LAF", e)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        }
        core.i18n.insert(this)

        frame {
            attr {
                title = "XMusic"
                setSize(800, 500)
                resizable
                setLocationRelativeTo(null)
            }
            val rootView = RootView(self)
            self.contentPane = rootView.root
        }.exitOnClose

    }

}