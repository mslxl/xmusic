package io.github.mslxl.xmusic.desktop

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.exitOnClose
import io.github.mslxl.ktswing.frame
import io.github.mslxl.ktswing.resizable
import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.i18n.I18N
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode
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

object App : I18N {
    const val version = "0.0.1-alpha"
    private val logger = App::class.logger
    override val id: String = XMusic.appID
    override val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>> = mapOf(
        "zh" to {
            listOf(
                "title" to "XMusic",
                "sidebar.my" to "我的",
                "sidebar.discovery" to "发现",
                "sidebar.setting" to "设置",
                "tab.about" to "关于",
                "my.my" to "我的收藏"
            )
        },
        "en" to {
            listOf(
                "title" to "XMusic",
                "sidebar.mine" to "Mine",
                "sidebar.discovery" to "Discovery",
                "sidebar.setting" to "Settings",
                "tab.about" to "About",
                "my.my" to "My Fav"
            )
        }
    )

    val core: XMusic = XMusic(LocalFile(), VlcjControl, CacheIndexDBImpl()).apply {
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