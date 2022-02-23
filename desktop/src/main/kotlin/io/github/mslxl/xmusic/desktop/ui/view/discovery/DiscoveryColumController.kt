package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.util.SequenceList
import io.github.mslxl.xmusic.desktop.ui.view.explorer.ExplorerCollectionView
import io.github.mslxl.xmusic.desktop.ui.view.findParent
import io.github.mslxl.xmusic.desktop.ui.view.root.RootView
import io.github.mslxl.xmusic.desktop.ui.view.songdetail.SongDetailView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.swing.DefaultListModel

class DiscoveryColumController<T : ExplorableIndex<E>, E : ExplorableEntity>(private val view: DiscoveryColumView<T, E>) {
    companion object {
        private val logger = DiscoveryColumController::class.logger
    }

    private var indexList: SequenceList<T>? = null
    private val listModel = DefaultListModel<ExplorableEntity>()


    init {
        view.scrollPane.horizontalScrollBar.addAdjustmentListener {
            if (it.valueIsAdjusting) return@addAdjustmentListener
            with(view.scrollPane.horizontalScrollBar) {
                val offset = visibleAmount
                val percentage = (it.value.toDouble() + offset) / maximum
                if (percentage > 0.8) {
                    fireLoad()
                }
            }
        }
        view.list.model = listModel
    }

    private fun createIndexList(after: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            logger.info("create explore index lazy list")
            indexList = SequenceList(arrayListOf(), view.processor.getExploredList(), Dispatchers.IO)
            indexList!!.addLoadSuccessListener {
                view.processor.getExploredDetail(it).forEach(listModel::addElement)
            }
            after.invoke()
        }
    }

    fun fireLoad() {
        fun fire() {
            logger.info("start load")
            indexList!!.load(10)
        }
        if (indexList == null) {
            createIndexList {
                fire()
            }
        } else {
            fire()
        }
    }

    fun openDetail(song: ExplorableEntity) {
        logger.info("open detail $song from discovery")
        val root = view.findParent<RootView>()!!
        val view = when (song) {
            is EntitySong -> SongDetailView(root, song)
            is EntityCollection -> ExplorerCollectionView(root, song)
            else -> error("Unrecognised explorable entity $song")
        }
        root.pushView(view)
    }
}