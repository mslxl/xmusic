package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.addon.processor.ExplorableEntity
import io.github.mslxl.xmusic.common.addon.processor.ExplorableIndex
import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.util.withRhinoContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeObject
import kotlin.coroutines.coroutineContext

class RhinoExplorerProcessor(val src: RhinoMusicSource, jsObj: NativeObject) :
    ExplorerProcessor<ExplorableIndex<ExplorableEntity>, ExplorableEntity> {
    init {
        assert(jsObj["exploreIter"] != null)
        assert(jsObj["exploreDetail"] != null)
    }

    private val explorerIterCreator = jsObj["exploreIter"] as Function
    private val exploreDetailCreator = jsObj["exploreDetail"] as Function

    override suspend fun getExploredList(): ReceiveChannel<ExplorableIndex<ExplorableEntity>> {
        return withRhinoContext { context ->
            val iter = RhinoJsIterWrapper(
                explorerIterCreator.call(
                    context,
                    src.scope,
                    src.scope,
                    emptyArray()
                ) as NativeObject, src.scope, ExplorableIndex::class
            )

            CoroutineScope(coroutineContext).produce {
                for (i in iter) {
                    @Suppress("UNCHECKED_CAST")
                    send(i as ExplorableIndex<ExplorableEntity>)
                }
            }
        }
    }

    override suspend fun getExploredDetail(index: ExplorableIndex<ExplorableEntity>): ReceiveChannel<ExplorableEntity> {
        return withRhinoContext { context ->
            val iter = RhinoJsIterWrapper(
                exploreDetailCreator.call(
                    context,
                    src.scope,
                    src.scope,
                    arrayOf(index)
                ) as NativeObject, src.scope, ExplorableEntity::class
            )
            CoroutineScope(coroutineContext).produce {
                for (i in iter) {
                    @Suppress("UNCHECKED_CAST")
                    send(i)
                }
            }
        }
    }
}