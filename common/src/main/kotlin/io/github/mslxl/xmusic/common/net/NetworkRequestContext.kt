package io.github.mslxl.xmusic.common.net

import org.apache.http.client.methods.HttpRequestBase

class NetworkRequestContext(val name: String, val block: (HttpRequestBase) -> Unit) {
    operator fun invoke(request: HttpRequestBase) {
        block.invoke(request)
    }
}

