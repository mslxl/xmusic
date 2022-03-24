package io.github.mslxl.xmusic.common.addon.rhino

import io.github.mslxl.xmusic.common.util.withRhinoContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeObject
import org.mozilla.javascript.ScriptableObject
import kotlin.reflect.KClass


class RhinoJsIterWrapper<T : Any>(jsObj: NativeObject, val scope: ScriptableObject, val kclass: KClass<T>) :
    Iterator<T> {
    companion object {
        inline operator fun <reified T : Any> invoke(
            jsObj: NativeObject,
            scope: ScriptableObject
        ): RhinoJsIterWrapper<T> {
            return RhinoJsIterWrapper(jsObj, scope, T::class)
        }
    }

    init {
        assert(jsObj["hasNext"] != null)
        assert(jsObj["next"] != null)
    }

    private val jsHasNext = jsObj["hasNext"] as Function
    private val jsNext = jsObj["next"] as Function

    override fun hasNext(): Boolean {
        return withRhinoContext { context ->
            jsHasNext.call(context, scope, scope, emptyArray()) as Boolean
        }
    }

    override fun next(): T {
        return withRhinoContext { context ->
            @Suppress("UNCHECKED_CAST")
            val v = jsNext.call(context, scope, scope, emptyArray())
            Context.jsToJava(v, kclass.java) as T
        }
    }
}