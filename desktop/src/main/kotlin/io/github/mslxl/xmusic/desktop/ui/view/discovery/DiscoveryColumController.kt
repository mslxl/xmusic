package io.github.mslxl.xmusic.desktop.ui.view.discovery

import io.github.mslxl.xmusic.common.addon.entity.EntityAlbum
import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.util.ChannelListAppender
import io.github.mslxl.xmusic.desktop.ui.view.explorer.ExplorerAlbumView
import io.github.mslxl.xmusic.desktop.ui.view.explorer.ExplorerCollectionView
import io.github.mslxl.xmusic.desktop.ui.view.findParent
import io.github.mslxl.xmusic.desktop.ui.view.root.RootView
import io.github.mslxl.xmusic.desktop.ui.view.songdetail.SongDetailView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import javax.swing.DefaultListModel

class DiscoveryColumController<T : ExplorableIndex<E>, E : ExplorableEntity>(private val view: DiscoveryColumView<T, E>) {
    companion object {
        private val logger = DiscoveryColumController::class.logger
    }

    private var listAppender: ChannelListAppender<T>? = null
    private val listModel = DefaultListModel<ExplorableEntity>()

    private var pauseLoad = false


    init {
        view.scrollPane.horizontalScrollBar.addAdjustmentListener {
            if (it.valueIsAdjusting) return@addAdjustmentListener
            with(view.scrollPane.horizontalScrollBar) {
                val offset = visibleAmount
                val percentage = (it.value.toDouble() + offset) / maximum
                if (percentage > 0.9) {
                    logger.info("scrollbar approach bottom(percentage $percentage), trigger load")
                    fireLoad()
                }
            }
        }
        view.list.model = listModel
    }

    private fun createIndexLoader(after: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch() {
            logger.info("create explore index loader")

            listAppender = ChannelListAppender({
                val details = view.processor.getExploredDetail(it)
                while (!details.isClosedForReceive) {
                    try {
                        val elem = details.receive()
                        listModel.addElement(elem)
                    } catch (_: ClosedReceiveChannelException) {
                    }
                }
            }, view.processor.getExploredList(), Dispatchers.IO).apply {
                addLoadSuccessListener {
                    pauseLoad = false
                }
            }

            after.invoke()
        }
    }

    fun fireLoad() {
        if (!pauseLoad) {
            fun fire() {
                pauseLoad = true
                listAppender!!.load(10)
            }
            if (listAppender == null) {
                createIndexLoader {
                    fire()
                }
            } else {
                fire()
            }
        } else {
            logger.info("load was paused by last task")
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