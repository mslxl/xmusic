package io.github.mslxl.xmusic.common.platform

import java.io.File

interface CacheIndexDBManager {
    fun get(key: String): File?

    /**
     * Copy [file] into cache directory, then save it into database
     */
    fun put(key: String, file: File)

    /**
     * Create a file, let [writer] write its content, then save it into database
     */
    fun put(key: String, writer: (File) -> Unit)

    /**
     * Create a file and init it if not exists, or return it
     */
    fun getOrInit(key: String, writer: (File) -> Unit): File


    fun getOrPut(key: String, download: (key: String) -> File): File {
        return get(key) ?: download.invoke(key).apply {
            put(key, this)
        }
    }

}
