package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.xmusic.common.addon.entity.EntityAlbum
import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.util.SequenceList
import io.github.mslxl.xmusic.desktop.ui.view.explorer.ExplorerAlbumView
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

    fun openDetail(item: ExplorableEntity) {
        logger.info("open detail $item from discovery")
        val root = view.findParent<RootView>()!!
        val view = when (item) {
            is EntitySong -> SongDetailView(root, item)
            is EntityCollection -> ExplorerCollectionView(root, item)
            is EntityAlbum -> ExplorerAlbumView(root, item)
            else -> error("Unrecognised explorable entity $item")
        }
        root.pushView(view)
    }
}