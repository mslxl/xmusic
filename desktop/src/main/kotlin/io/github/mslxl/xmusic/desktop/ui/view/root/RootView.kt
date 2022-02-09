package io.github.mslxl.xmusic.desktop.ui.view.root

import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.cardLayout
import io.github.mslxl.xmusic.common.i18n.i18n
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import io.github.mslxl.xmusic.desktop.ui.view.collection.CollectionView
import io.github.mslxl.xmusic.desktop.ui.view.discovery.DiscoveryView
import io.github.mslxl.xmusic.desktop.ui.view.settings.SettingView
import java.awt.CardLayout
import java.util.*
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel

class RootView(private val frame: JFrame) : View {
    companion object {
        private val logger = RootView::class.logger

    }

    enum class InternalCard {
        CARD_NAME_SETTINGS,
        CARD_NAME_COLLECTION,
        CARD_NAME_DISCOVERY
    }

    override val parent: View? = null

    private var cardLayout: CardLayout
    private var centerPane: JPanel
    private var currentShowCard = ""
    private fun showInternalCard(card: InternalCard) {
        val view = when (card) {
            InternalCard.CARD_NAME_DISCOVERY -> viewDiscovery
            InternalCard.CARD_NAME_SETTINGS -> viewSetting
            InternalCard.CARD_NAME_COLLECTION -> viewCollection
        }
        pushView(view)
    }

    private val stack = Stack<View>()
    fun pushView(view: View) {
        val id = view.hashCode().toString()
        if (view in stack) {
            stack.remove(view)
            logger.info("raise card ${view::class.simpleName} to top")
        } else {
            logger.info("push card ${view::class.simpleName}")
            centerPane.add(view.root, id)
        }
        stack.push(view)
        cardLayout.show(centerPane, id)
        currentShowCard = id
    }

    fun popView() {
        if (stack.size > 1) {
            val view = stack.pop()
            val peek = stack.peek()
            logger.info("pop card ${view::class.simpleName}, now peek is ${peek::class.simpleName}")
            centerPane.remove(view.root)
            cardLayout.show(centerPane, peek.hashCode().toString())
        }
    }


    private val viewSideBar = SideBarView(this).apply {
        addAction("\uf106", "") {
            popView()
        }
        addAction("\uf002", "sidebar.discovery".i18n(App.core, App.id)) {
            showInternalCard(InternalCard.CARD_NAME_DISCOVERY)
        }
        addAction("\uf004", "sidebar.my".i18n(App.core, App.id)) {
            showInternalCard(InternalCard.CARD_NAME_COLLECTION)
        }
        addAction("\uf4fe", "sidebar.setting".i18n(App.core, App.id)) {
            showInternalCard(InternalCard.CARD_NAME_SETTINGS)
        }
    }
    private val viewPlayBar = PlayBarView(this)

    private val viewCollection = CollectionView(this)

    private val viewDiscovery = DiscoveryView(this)

    private val viewSetting = SettingView(this)

    override val root = swing<JComponent> {
        panelWith(borderLayout()) {
            left {
                addView(viewSideBar)
            }
            center {
                centerPane = panel {
                    cardLayout = cardLayout {
                    }
                }

                showInternalCard(InternalCard.CARD_NAME_COLLECTION)
            }
            bottom {
                addView(viewPlayBar)
            }
        }
    }
}