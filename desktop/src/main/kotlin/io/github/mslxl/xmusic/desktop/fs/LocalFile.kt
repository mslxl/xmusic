package io.github.mslxl.xmusic.desktop.fs

import io.github.mslxl.xmusic.common.fs.FileSystem
import io.github.mslxl.xmusic.desktop.EnvVar
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class LocalFile : FileSystem {
    private val dir = File(EnvVar.programCfgDir, "src").apply {
        if (!exists()) mkdirs()
    }

    override fun list(folders: List<String>): List<String> {
        return File(dir, folders.reduce { acc, s -> acc + File.pathSeparator + s })
            .list()
            .toList()
    }

    override fun openInputStream(folders: List<String>, name: String): InputStream {
        return File(dir, folders.joinToString(File.pathSeparator) + "/$name").apply {
            if (!exists()) {
                parentFile.mkdirs()
                createNewFile()
            }
        }.inputStream()
    }

    override fun openOutputStream(folders: List<String>, name: String): OutputStream {
        return File(dir, folders.joinToString(File.pathSeparator) + "/$name").apply {
            if (!exists()) {
                parentFile.mkdirs()
                createNewFile()
            }
        }.outputStream()
    }
}