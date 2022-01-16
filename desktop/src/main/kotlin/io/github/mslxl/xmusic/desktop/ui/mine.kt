package io.github.mslxl.xmusic.desktop.ui

import io.github.mslxl.ktswing.attr
import io.github.mslxl.ktswing.component.adv.lazyPanel
import io.github.mslxl.ktswing.component.list
import io.github.mslxl.ktswing.component.scrollPane
import io.github.mslxl.ktswing.component.splitPane
import io.github.mslxl.ktswing.component.tabbedPane
import io.github.mslxl.ktswing.group.swing
import io.github.mslxl.ktswing.layout.borderLayoutCenter
import io.github.mslxl.xmusic.common.entity.EntityCollection
import io.github.mslxl.xmusic.desktop.App
import io.github.mslxl.xmusic.desktop.ui.compoent.JSongTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.ListSelectionModel

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
                                val defModel = DefaultListModel<String>()

                                val leftList: JList<String>
                                val rightTable = JSongTable(src, App.core.playlist)
                                val collectionDataList = arrayListOf<EntityCollection>()
                                scrollPane {
                                    leftList = list<String>(defModel) {
                                        attr {
                                            selectionMode = ListSelectionModel.SINGLE_SELECTION
                                            selectionModel.addListSelectionListener { selectEvent ->
                                                if (selectEvent.valueIsAdjusting) return@addListSelectionListener
                                                // Show collection's content
                                                GlobalScope.launch(Dispatchers.IO) {
                                                    val entity = collectionDataList[self.selectedIndex]
                                                    val seq = src.collection!!.getContent(entity)
                                                    withContext(Dispatchers.Swing) {
                                                        rightTable.setDataSource(seq)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // Load all collection name
                                GlobalScope.launch(Dispatchers.IO) {
                                    src.collection!!.getAllCollection().forEach {
                                        //TODO lazy load
                                        val name = src.collection!!.getName(it)
                                        defModel.addElement(name)
                                        collectionDataList.add(it)
                                    }
                                }
                                // Create song table
                                add(rightTable)
                            }
                        }
                    }
                }
            }
        }
    }
}

