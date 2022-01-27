package io.github.mslxl.xmusic.desktop.ui.view.root

import io.github.mslxl.ktswing.component.panel
import io.github.mslxl.ktswing.component.panelWith
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayout
import io.github.mslxl.ktswing.layout.cardLayout
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.ui.view.View
import io.github.mslxl.xmusic.desktop.ui.view.addView
import io.github.mslxl.xmusic.desktop.ui.view.collection.CollectionView
import io.github.mslxl.xmusic.desktop.ui.view.discovery.DiscoveryView
import io.github.mslxl.xmusic.desktop.ui.view.settings.SettingView
import java.awt.CardLayout
import javax.swing.JComponent
import javax.swing.JPanel

class RootView : View {
    companion object {
        private val logger = RootView::class.logger

        const val CARD_NAME_SETTINGS = "settings"
        const val CARD_NAME_COLLECTION = "collection"
        const val CARD_NAME_DISCOVERY = "discovery"
    }

    private var cardLayout: CardLayout
    private var centerPane: JPanel
    private var currentShowCard = ""
    private fun showCard(cardName: String) {
        logger.info("show card $cardName")
        cardLayout.show(centerPane, cardName)
        centerPane.updateUI()
        currentShowCard = cardName
    }

    private val viewSideBar = SideBarView().apply {
        addAction("\uf002", "Discovery") {
            showCard(CARD_NAME_DISCOVERY)
        }
        addAction("\uf004", "Collection") {
            showCard(CARD_NAME_COLLECTION)
        }
        addAction("\uf4fe", "Setting") {
            showCard(CARD_NAME_SETTINGS)
        }
    }
    private val viewPlayBar = PlayBarView()

    private val viewCollection = CollectionView()

    private val viewDiscovery = DiscoveryView()

    private val viewSetting = SettingView()

    override val root = swing<JComponent> {
        panelWith(borderLayout()) {
            left {
                addView(viewSideBar)
            }
            center {
                centerPane = panel {
                    cardLayout = cardLayout {
                        card(CARD_NAME_SETTINGS) {
                            addView(viewSetting)
                        }
                        card(CARD_NAME_COLLECTION) {
                            addView(viewCollection)
                        }
                        card(CARD_NAME_DISCOVERY) {
                            addView(viewDiscovery)
                        }
                        show(CARD_NAME_DISCOVERY)
                    }
                }
            }
            bottom {
                addView(viewPlayBar)
            }
        }
    }
}