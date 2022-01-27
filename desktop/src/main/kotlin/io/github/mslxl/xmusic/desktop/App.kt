package io.github.mslxl.xmusic.desktop

import com.bulenkov.darcula.DarculaLaf
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.exitOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.resizable
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.src.SourceLocalMusic
import io.github.mslxl.xmusic.desktop.fs.CacheIndexDBImpl
import io.github.mslxl.xmusic.desktop.fs.LocalFile
import io.github.mslxl.xmusic.desktop.player.VlcjControl
import io.github.mslxl.xmusic.desktop.ui.util.initGlobalFont
import io.github.mslxl.xmusic.desktop.ui.view.root.RootView
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.UIManager

object App {
    const val version = "0.0.1-alpha"
    private val logger = App::class.logger

    val core: XMusic = XMusic(LocalFile(), VlcjControl, CacheIndexDBImpl()).apply {
        addMusicSource(SourceLocalMusic(this))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("XMusic desktop($version) start")
        try {
            initGlobalFont()
            UIManager.setLookAndFeel(DarculaLaf())
            JFrame.setDefaultLookAndFeelDecorated(true)
            JDialog.setDefaultLookAndFeelDecorated(true)
        } catch (e: ExceptionInInitializerError) {
            logger.warn("Fail to use darcula LAF", e)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        }
        val rootView = RootView()

        frame {
            attr {
                title = "XMusic"
                setSize(800, 500)
                resizable
                setLocationRelativeTo(null)
            }
            self.contentPane = rootView.root
        }.exitOnClose

    }
}