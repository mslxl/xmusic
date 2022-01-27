package io.github.mslxl.xmusic.desktop.ui.view.settings

import io.github.mslxl.ktswing.component.TabbedPaneScope
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import javax.swing.JTabbedPane

class SettingView : View {
    private val controller = SettingController(this)
    private val viewAbout = AboutView()
    private val viewProgramConfig = ProgramConfigView()
    override val root = swing<JTabbedPane> {
        tabbedPane {
            tabPanelWith("XMusic", borderLayoutCenter()) {
                addView(viewProgramConfig)
            }
            musicSourceConfigTabs()
            tabPanelWith("About", borderLayoutCenter()) {
                addView(viewAbout)
            }
        }
    }

    private fun TabbedPaneScope.musicSourceConfigTabs() {
        controller.src.forEach { sourceConfig ->
            val srcName = App.core.getSrc(sourceConfig.id).name
            tabPanelWith(srcName, borderLayoutCenter()) {
                addView(SourceConfigTabView(sourceConfig))
            }
        }
    }

}