package io.github.mslxl.xmusic.desktop.src

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.entity.EntityCollection
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 */
class SourceLocalMusic : MusicSource {
    companion object {
        private val logger = SourceLocalMusic::class.logger
    }

    lateinit var config: SourceConfig

    override val name: String
        get() = "LocalMusic"


    override val information = object : SongProcessor {
        override suspend fun getInfo(entitySong: EntitySong): List<EntitySongInfo> {
            return suspendCoroutine { continuation ->
                val file = File(entitySong.id)
                val name = file.nameWithoutExtension
                logger.info("Get info from file name '$name'")
                if ('-' in name) {
                    val (singer, title) = name.split('-', limit = 2).map(String::trim)
                    continuation.resume(
                        listOf(
                            EntitySongInfo(
                                id = entitySong.id, title = title, singer = singer, coverUrl = ""
                            )
                        )
                    )
                } else {
                    continuation.resume(
                        listOf(
                            EntitySongInfo(
                                id = entitySong.id, title = name, singer = "Unknown", coverUrl = ""
                            )
                        )
                    )
                }
            }
        }

        override suspend fun getURL(entitySong: EntitySong, info: EntitySongInfo, option: String): URL {
            return File(entitySong.id).toURI().toURL()
        }
    }

    override val collection = object : CollectionProcessor {
        val musicCatalog = hashMapOf<String, List<File>>()

        fun listMusicDir(root: File, base: File): Sequence<String> {
            val relPath = base.toRelativeString(root)
            val fileList = arrayListOf<File>()
            musicCatalog[relPath] = fileList
            logger.info("Scanning local music in ${base.absolutePath}")
            return sequence {
                yield(base.toRelativeString(root))
                base.listFiles { file: File ->
                    file.isDirectory || file.extension in XMusic.acceptExt
                }?.forEach { file ->
                    if (file.isFile) {
                        fileList.add(file)
                        logger.info("Found ${file.nameWithoutExtension} in catalog $relPath")
                    } else {
                        yieldAll(listMusicDir(root, file))
                    }
                }
            }
        }

        override suspend fun getAllCollection(): Sequence<EntityCollection> {
            return config.getNullable("path")?.let { path ->
                val root = File(path)
                // The code that I wrote is so ugly
                withContext(Dispatchers.IO) {
                    listMusicDir(root, root).map {
                        EntityCollection(it, this@SourceLocalMusic.id)
                    }
                }
            } ?: emptySequence()
        }

        override suspend fun getName(entity: EntityCollection): String {
            return entity.id.ifBlank { "Uncatalogued" }
        }

        override suspend fun getContent(entity: EntityCollection): Sequence<EntitySong> {
            return musicCatalog[entity.id]!!.map {
                EntitySong(it.absolutePath, this@SourceLocalMusic.id)
            }.asSequence()
        }
    }

    override fun acceptConfig(config: SourceConfig) {
        config.markType("path", "Music Folder", SourceConfig.ItemType.TEXT)
        this.config = config
    }
}