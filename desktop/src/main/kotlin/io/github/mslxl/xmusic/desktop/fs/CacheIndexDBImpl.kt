package io.github.mslxl.xmusic.desktop.fs

import io.github.mslxl.xmusic.common.fs.CacheIndexDBManager
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.EnvVar
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.sql.DriverManager

class CacheIndexDBImpl : CacheIndexDBManager {
    companion object {
        private val logger = CacheIndexDBImpl::class.logger

        private val cacheDir = File(EnvVar.programCfgDir, "cache").apply {
            if (!exists()) mkdirs()
        }
        private val indexFile = File(cacheDir, "index.db")
        private val connection = DriverManager.getConnection("jdbc:sqlite:${indexFile.absolutePath}")
    }


    init {
        logger.info("init cache database")
        connection.createStatement().use {
            it.execute("create table if not exists cache (hash integer, path string) ")
        }
    }

    override fun get(key: String): File? {
        val md5 = try {
            connection.createStatement().use {
                val resultSet = it.executeQuery("select * from cache where hash=${key.hashCode()}")
                resultSet.getString("path")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val f = File(cacheDir, md5)
        return if (f.exists()) f else null
    }

    private fun insert(key: String, baseName: String) {
        connection.createStatement().use {
            it.execute("insert into cache values(${key.hashCode()}, '$baseName')")
        }
    }


    override fun put(key: String, file: File) {
        val md5 = file.inputStream().use {
            DigestUtils.md5Hex(it)
        }
        val dst = File(cacheDir, md5)
        if (!dst.exists()) {
            file.copyTo(dst)
        }
        insert(key, md5)
    }

    override fun put(key: String, writer: (File) -> Unit) {
        val tmpFile = File(cacheDir, "${key.hashCode().toString(16)}.tmp")
        try {
            writer.invoke(tmpFile)
        } catch (e: Exception) {
            tmpFile.delete()
            throw e
        }
        val md5 = tmpFile.inputStream().use {
            DigestUtils.md5Hex(it)
        }
        val dst = File(cacheDir, md5)
        if (!dst.exists()) {
            tmpFile.renameTo(dst)
        }
        insert(key, md5)
    }

    override fun getOrInit(key: String, writer: (File) -> Unit): File {
        return get(key) ?: let {
            put(key, writer)
            get(key)!!
        }
    }

}