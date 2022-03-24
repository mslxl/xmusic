package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.i18n.I18NKey
import org.mozilla.javascript.NativeObject

fun RhinoDiscoveryProcessor(src: RhinoMusicSource): Map<I18NKey, ExplorerProcessor<*, *>>? {
    val processors = src.processors["explorer"] as? NativeObject ?: return null
    return processors.keys.map {
        val processor = processors[it] as NativeObject
        it!!.toString() to constructExplorerProcessor(src, processor)
    }.toMap()
}

private fun constructExplorerProcessor(src: RhinoMusicSource, obj: NativeObject): RhinoExplorerProcessor {
    return RhinoExplorerProcessor(src, obj)
}