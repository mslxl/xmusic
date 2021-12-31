package io.github.mslxl.xmusic.common.fs

import java.io.InputStream
import java.io.OutputStream

interface FileSystem {
    fun write(folders: List<String> = listOf(), name: String, inputStream: InputStream)
    fun write(folders: List<String> = listOf(), name: String, content: String) {
        write(folders, name, content.byteInputStream())
    }

    fun list(folders: List<String>): List<String>

    fun readString(folders: List<String>, name: String): String {
        return open(folders, name).bufferedReader().readText()
    }

    fun open(folders: List<String>, name: String): InputStream
}