package io.github.mslxl.xmusic.desktop.ui.view.mine

import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.splitPane
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JCollectionList
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import javax.swing.JComponent

fun myFavPane(): JComponent {
    val srcSupportCollection = App.core.sourceList.map {
        App.core.getSrc(it)
    }.filter {
        it.collection != null
    }

    return swing {
        tabbedPane {
            tabPanel("My") {

            }
            srcSupportCollection.forEach { src ->
                tabPanelWith(src.name, borderLayoutCenter()) {
                    lazyPanel {
                        borderLayoutCenter {
                            splitPane {
                                allSplitPane {
                                    it.dividerSize = 2
                                }
                                val leftList = JCollectionList(src.collection!!)
                                val rightTable = JSongTable(src, App.core.playlist)

                                leftList.addSelectionChangeListener { index, entityCollection ->
                                    entityCollection?.let {
                                        GlobalScope.launch(Dispatchers.IO) {
                                            val seq = src.collection!!.getContent(it.index)
                                            withContext(Dispatchers.Swing) {
                                                rightTable.setDataSource(seq)
                                            }
                                        }
                                    }
                                }
                                // Create song table
                                add(leftList)
                                add(rightTable)

                                // prepare current collection data
                                GlobalScope.launch(Dispatchers.IO) {
                                    val allCollectionIndex = src.collection!!.getAllCollection()
                                    leftList.setData(allCollectionIndex)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

