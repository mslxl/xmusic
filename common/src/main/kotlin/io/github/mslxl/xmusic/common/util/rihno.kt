package io.github.mslxl.xmusic.common.util

import org.mozilla.javascript.Context

object RhinoContextThreadManager {
    val contextNumber = ThreadLocal.withInitial { 0 }
    var context = ThreadLocal<Context?>()
}

inline fun <R> withRhinoContext(block: (Context) -> R): R {
    return with(RhinoContextThreadManager) {
        val numInstanceBefore = contextNumber.get()
        if (numInstanceBefore == 0)
            context.set(Context.enter())
        val ctx = context.get()!!
        val ret = block.invoke(ctx)

        contextNumber.set(contextNumber.get() - 1)
        if (contextNumber.get() == 0) {
            Context.exit()
            context.set(null)
        }
        ret
    }
}
