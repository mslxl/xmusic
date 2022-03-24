package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.util.withRhinoContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeObject
import kotlin.coroutines.coroutineContext

class RhinoCollectionProcessor(val src: RhinoMusicSource) : CollectionProcessor {
    val processor = src.processors["collection"]!! as NativeObject

    private val allCollectionIterCreator = processor["collectionIter"]!! as Function
    private val getDetail = processor["getDetail"]!! as Function
    private val getContentIter = processor["getContentIter"]!! as Function

    override suspend fun getAllCollection(): ReceiveChannel<EntityCollectionIndex> {
        return CoroutineScope(coroutineContext).produce {
            withRhinoContext { context ->
                val jsObjIter =
                    allCollectionIterCreator.call(context, src.scope, src.scope, emptyArray()) as NativeObject
                val iter = RhinoJsIterWrapper<EntityCollectionIndex>(jsObjIter, src.scope)
                for (i in iter) {
                    println(i)
                    send(i)
                }
            }
        }
    }

    override suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection {
        return withRhinoContext { context ->
            val obj = getDetail.call(context, src.scope, src.scope, arrayOf(entity))
            Context.jsToJava(obj, EntityCollection::class.java) as EntityCollection
        }
    }

    override suspend fun getContent(entity: EntityCollectionIndex): ReceiveChannel<EntitySongIndex> {
        return CoroutineScope(coroutineContext).produce {
            withRhinoContext { context ->
                val jsObjIter =
                    getContentIter.call(context, src.scope, src.scope, emptyArray()) as NativeObject

                val iter = RhinoJsIterWrapper<EntitySongIndex>(jsObjIter, src.scope)
                for (i in iter) {
                    send(i)
                }
            }
        }
    }
}