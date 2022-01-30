package io.github.mslxl.xmusic.common.util

import io.github.mslxl.xmusic.common.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SequenceList<E>(
    private val list: MutableList<E>,
    sequence: Sequence<E>,
    private val context: CoroutineContext = Dispatchers.Unconfined
) : List<E> by list {
    companion object {
        private val logger = SequenceList::class.logger
    }

    private var isSequenceFinish = false
    private var job: Job? = null
    private var waitLoad = 0
    private val iterator = sequence.iterator()
    private val listener = linkedSetOf<suspend (E) -> Unit>()

    private fun startLoader() {
        if (job == null || job?.isActive == false) {
            job = GlobalScope.launch(context) {
                logger.info("sequence list load, task num: $waitLoad")
                while (waitLoad > 0 && iterator.hasNext()) {
                    waitLoad--
                    val elem = iterator.next()
                    list.add(elem)
                    listener.forEach {
                        try {
                            it.invoke(elem)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }
                isSequenceFinish = !iterator.hasNext()
                job = null
            }
        }
    }

    fun addLoadSuccessListener(listener: suspend (E) -> Unit) {
        this.listener.add(listener)
    }

    fun load(len: Int = 1) {
        if (!isSequenceFinish) {
            waitLoad += len
            startLoader()
        }
    }
}