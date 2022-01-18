package io.github.mslxl.xmusic.common.fs

import java.io.File

interface CacheIndexDBManager {
    fun get(key: String): File?

    /**
     * Copy [file] into cache directory, then save it into database
     */
    fun put(key: String, file: File)

    fun createFile(key: String, writer: (File) -> Unit): File

    fun getOrPut(path: String, download: (path: String) -> File): File {
        return get(path) ?: download.invoke(path).apply {
            put(path, this)
        }
    }

}
