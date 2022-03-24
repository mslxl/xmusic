package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.XMusicEventRegister
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.events.XMusicInitializationEvent
import io.github.mslxl.xmusic.common.events.XMusicPostinitializationEvent
import io.github.mslxl.xmusic.common.i18n.I18N
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.net.NetworkHandle
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.ScriptableObject

class RhinoMusicSource(scriptName: String, scriptText: String) : MusicSource {
    lateinit var core: XMusic
    val scope: ScriptableObject
    val exportObj: NativeObject

    /**
     * export.processor from js file
     */
    val processors: NativeObject

    val network = NetworkHandle(this)

    init {
        val context = Context.enter()
        scope = ImporterTopLevel(context)
        scope.put("network", scope, Context.javaToJS(network, scope))
        scope.put("logger", scope, Context.javaToJS(logger("Rhino Engine-${scriptName}"), scope))
        context.evaluateString(scope, scriptText, scriptName, 0, null)
        exportObj = scope.get("export") as NativeObject
        processors = exportObj["processor"] as NativeObject
        Context.exit()
    }

    override val name: I18NKey
        get() = exportObj["name"]!!.toString()
    override val id: SourceID
        get() = exportObj["id"]?.toString() ?: name.hashCode().toString()

    @XMusicEventRegister
    fun init(event: XMusicInitializationEvent) {
        core = event.source
    }

    @XMusicEventRegister
    fun postInit(event: XMusicPostinitializationEvent) {
        exportObj["lang"]?.let { lang ->
            val lang = lang as NativeObject
            core.i18n.insert(object : I18N {
                override val id: String
                    get() = this@RhinoMusicSource.id
                override val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>>
                    get() = lang.keys.asSequence().map { langCode ->
                        val value = lang[langCode!!.toString()] as NativeObject
                        langCode.toString() to {
                            value.toList().map { it.first.toString() to it.second.toString() }
                        }
                    }.toMap()
            })
        }
    }

    // Addition feature below
    override val information = RhinoSongInfoProcessor(this)

    override val collection: CollectionProcessor? by lazy {
        if (processors["collection"] == null) return@lazy null
        RhinoCollectionProcessor(this)
    }
    override val discovery: Map<I18NKey, ExplorerProcessor<*, *>>? by lazy {
        if (processors["explorer"] == null) return@lazy null
        RhinoDiscoveryProcessor(this)
    }
}


