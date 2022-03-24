package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.addon.processor.SongProcessor
import io.github.mslxl.xmusic.common.util.withRhinoContext
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject
import java.net.URL

class RhinoSongInfoProcessor(private val src: RhinoMusicSource) : SongProcessor {
    init {
        assert(src.processors["song"] != null)
    }

    val processor = src.processors["song"] as NativeObject

    @Suppress("UNCHECKED_CAST")
    val detail = processor["getDetail"] as org.mozilla.javascript.Function

    @Suppress("UNCHECKED_CAST")
    val url = processor["getURL"] as org.mozilla.javascript.Function
    override suspend fun getDetail(entitySongPreview: EntitySongIndex): List<EntitySong> {
        return withRhinoContext { context ->
            detail.call(context, src.scope, src.scope, arrayOf(entitySongPreview)) as NativeArray
        }.map { it as EntitySong }.toList()
    }

    override suspend fun getURL(info: EntitySong, option: String): URL {
        TODO()
    }
}