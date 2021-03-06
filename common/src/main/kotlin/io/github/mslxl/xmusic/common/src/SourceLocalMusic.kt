package io.github.mslxl.xmusic.common.src

import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.SourceID
import io.github.mslxl.xmusic.common.addon.XMusicEventRegister
import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.addon.processor.SongProcessor
import io.github.mslxl.xmusic.common.config.ConfigurationFactory
import io.github.mslxl.xmusic.common.events.XMusicInitializationEvent
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode
import io.github.mslxl.xmusic.common.i18n.i18n
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.util.MusicUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.File
import java.net.URL
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 */
class SourceLocalMusic() : MusicSource {
    lateinit var core: XMusic

    companion object {
        private val logger = SourceLocalMusic::class.logger
        const val id = "io.github.mslxl.xmusic.common.localmusic"
    }

    override val name: String = "localmusic.name"
    override val id: SourceID = SourceLocalMusic.id

    override val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>> = mapOf(
        "zh_CN" to {
            listOf(
                "localmusic.name" to "本地音乐",
                "localmusic.item.uncatalogued" to "未分类"
            )
        },
        "en" to {
            listOf(
                "localmusic.name" to "Local Music",
                "localmusic.item.uncatalogued" to "Uncatalogued"
            )
        })

    override val configuration by lazy {
        ConfigurationFactory().buildFrom<SourceLocalMusicConfiguration>(id)
    }

    @XMusicEventRegister
    fun init(event: XMusicInitializationEvent) {
        core = event.source
        core.i18n.insert(this)
    }

    override val information = object : SongProcessor {
        override suspend fun getDetail(entitySongPreview: EntitySongIndex): List<EntitySong> {
            return suspendCoroutine { continuation ->
                val file = File(entitySongPreview.id)
                val name = file.nameWithoutExtension
                val cover = if (file.extension == "mp3") {
                    MusicUtils.getCoverFromMp3(file, core.cacheManager).toURI().toURL()
                } else {
                    MusicUtils.defaultCover.toURI().toURL()
                }
                logger.info("Get title and singer from filename '$name'")
                if ('-' in name) {
                    val (singer, title) = name.split('-', limit = 2).map(String::trim)
                    continuation.resume(
                        listOf(
                            EntitySong(
                                index = entitySongPreview,
                                id = entitySongPreview.id,
                                title = title,
                                singer = singer,
                                cover = cover
                            )
                        )
                    )
                } else {
                    continuation.resume(
                        listOf(
                            EntitySong(
                                index = entitySongPreview,
                                id = entitySongPreview.id,
                                title = name,
                                singer = "Unknown",
                                cover = cover
                            )
                        )
                    )
                }
            }
        }

        override suspend fun getURL(info: EntitySong, option: String): URL {
            return File(info.index.id).toURI().toURL()
        }
    }

    override val collection = object : CollectionProcessor {
        val musicCatalog = hashMapOf<String, List<File>>()

        fun scanMusic(root: File, base: File): Sequence<String> {
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
                        yieldAll(scanMusic(root, file))
                    }
                }
            }

        }

        override suspend fun getAllCollection(): ReceiveChannel<EntityCollectionIndex> {
            val path = configuration.path
            val root = File(path)
            return CoroutineScope(coroutineContext).produce {
                scanMusic(root, root).forEach {
                    send(EntityCollectionIndex(it, this@SourceLocalMusic.id))
                }
            }
        }

        override suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection {
            return EntityCollection(
                index = entity,
                name = entity.id.ifBlank { "localmusic.item.uncatalogued".i18n(core, this@SourceLocalMusic.id) },
                desc = "Local music in folder ${entity.id}",
                creator = "Filesystem"
            )
        }

        override suspend fun getContent(entity: EntityCollectionIndex): ReceiveChannel<EntitySongIndex> {
            return CoroutineScope(coroutineContext).produce {
                musicCatalog[entity.id]!!.forEach {
                    send(EntitySongIndex(it.absolutePath, this@SourceLocalMusic.id))
                }
            }

        }
    }
}