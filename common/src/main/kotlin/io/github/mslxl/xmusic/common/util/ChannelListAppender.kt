package io.github.mslxl.xmusic.common.util

import io.github.mslxl.xmusic.common.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ChannelListAppender<E>(
    private val appender: suspend (E) -> Unit,
    private val channel: ReceiveChannel<E>,
    private val context: CoroutineContext = Dispatchers.Unconfined
) {
    companion object {
        private val logger = ChannelListAppender::class.logger
    }

    private var job: Job? = null
    private var loadRemain = 0

    val listener = linkedSetOf<suspend () -> Unit>()

    private fun startLoader() {
        if (job == null || job?.isActive == false) {
            job = CoroutineScope(context).launch {
                logger.info("load $loadRemain element in channel list")
                while (loadRemain > 0 && !channel.isClosedForReceive) {
                    loadRemain--
                    val elem = try {
                        channel.receive()
                    } catch (_: ClosedReceiveChannelException) {
                        continue
                    }

                    appender.invoke(elem)

                    // call listener
                }
                job = null
                listener.forEach {
                    try {
                        it.invoke()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun addLoadSuccessListener(listener: suspend () -> Unit) {
        this.listener.add(listener)
    }

    fun load(len: Int = 1) {
        if (!channel.isClosedForReceive) {
            logger.info("append $len task")
            loadRemain += len
            startLoader()
        }
    }
}