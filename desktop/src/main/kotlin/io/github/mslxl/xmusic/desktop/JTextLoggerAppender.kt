package io.github.mslxl.xmusic.desktop

import ch.qos.logback.core.UnsynchronizedAppenderBase

class JTextLoggerAppender<E> : UnsynchronizedAppenderBase<E>() {
    companion object {
        private val listener = arrayListOf<(String) -> Unit>()
        fun broadcast(text: String) {
            listener.forEach {
                try {
                    it.invoke(text)
                } catch (_: Exception) {
                }
            }
        }

        fun registerNotifier(listener: (String) -> Unit) {
            this.listener.add(listener)
        }

        fun unregisterNotifier(listener: (String) -> Unit) {
            this.listener.remove(listener)
        }
    }

    override fun append(eventObject: E) {
        broadcast(eventObject.toString())
    }

}