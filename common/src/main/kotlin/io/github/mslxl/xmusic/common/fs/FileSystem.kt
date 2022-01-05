package io.github.mslxl.xmusic.common.fs

import java.io.InputStream
import java.io.OutputStream

interface FileSystem {
    fun write(folders: List<String> = listOf(), name: String, data: InputStream) {
        openOutputStream(folders, name).use {
            data.copyTo(it)
        }
    }

    fun write(folders: List<String> = listOf(), name: String, content: String) {
        write(folders, name, content.byteInputStream())
    }

    /**
     * Get all files in folder
     */
    fun list(folders: List<String>): List<String>

    fun readString(folders: List<String>, name: String): String {
        return openInputStream(folders, name).bufferedReader().readText()
    }

    fun openInputStream(folders: List<String>, name: String): InputStream
    fun openOutputStream(folders: List<String>, name: String): OutputStream
}