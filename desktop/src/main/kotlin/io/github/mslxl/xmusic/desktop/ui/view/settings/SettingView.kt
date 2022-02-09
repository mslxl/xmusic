package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.component.TabbedPaneScope
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.i18n.i18n
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import javax.swing.JTabbedPane

class SettingView(override val parent: View?) : View {
    private val controller = SettingController(this)
    private val viewAbout = AboutView(this)
    private val viewProgramConfig = ProgramConfigView(this)
    override val root = swing<JTabbedPane> {
        tabbedPane {
            tabPanelWith("title".i18n(App.core, App.id) /* XMusic in default */, borderLayoutCenter()) {
                addView(viewProgramConfig)
            }
            musicSourceConfigTabs()
            tabPanelWith("tab.about".i18n(App.core, App.id), borderLayoutCenter()) {
                addView(viewAbout)
            }
        }
    }

    private fun TabbedPaneScope.musicSourceConfigTabs() {
        controller.src.forEach { sourceConfig ->
            val src = App.core.getSrc(sourceConfig.id)
            tabPanelWith(src.name.i18n(App.core, src.id), borderLayoutCenter()) {
                addView(SourceConfigTabView(sourceConfig, this@SettingView))
            }
        }
    }

}