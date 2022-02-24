package io.github.mslxl.xmusic.common.net

import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.manager.PlatformMan
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.HttpClients
import java.io.File
import java.net.URL
import java.util.*

class NetworkHandle internal constructor(val src: MusicSource) {
    companion object {
        private val instance = HashMap<SourceID, NetworkHandle>()

        @JvmStatic
        fun require(id: SourceID): NetworkHandle {
            return instance.getOrPut(id) {
                NetworkHandle(AddonsMan.getInstance<MusicSource>(id)!!)
            }
        }

        @JvmStatic
        fun require(src: MusicSource): NetworkHandle {
            return instance.getOrPut(src.id) {
                NetworkHandle(src)
            }
        }
    }

    private val logger = NetworkHandle::class.logger
    private val contexts = LinkedList<NetworkRequestContext>()

    init {
        this + NetworkRequestContext("FirefoxUserAgent") {
            it.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:97.0) Gecko/20100101 Firefox/97.0")
        }
    }

    private fun LinkedList<NetworkRequestContext>.initWith(request: HttpRequestBase) {
        forEach {
            logger.info("apply ${it.name} to ${src.id}'s request")
            it.block.invoke(request)
        }
    }

    fun get(url: URL, vararg additionalContext: NetworkRequestContext): CloseableHttpResponse {
        val client = HttpClients.createSystem()
        val post = HttpGet(url.toURI())
        contexts.initWith(post)
        return client.execute(post)
    }

    fun post(url: URL, vararg additionalContext: NetworkRequestContext): CloseableHttpResponse {
        val client = HttpClients.createSystem()
        val post = HttpPost(url.toURI())
        contexts.initWith(post)
        return client.execute(post)
    }


    @JvmOverloads
    fun download(url: URL, allowCache: Boolean = true): File {
        logger.info("Receive download request \"$url\" from source ${src.name}(${src.id})")
        if (url.protocol == "file") {
            logger.info("Download finish: \"$url\" is already a local file")
            return File(url.toURI())
        }
        fun dl(file: File) {
            val client = HttpClients.createSystem()
            val request = HttpGet(url.toURI())
            contexts.initWith(request)
            val response = client.execute(request)
            response.entity.content.copyTo(file.outputStream())
        }
        return if (allowCache) {
            PlatformMan.cacheDB.getOrInit(url.toString()) {
                dl(it)
            }
        } else {
            File.createTempFile(url.hashCode().toString(), "").apply {
                dl(this)
            }
        }
    }

    operator fun plus(ctx: NetworkRequestContext) {
        contexts.add(ctx)
    }
}