import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.XMusicEventRegister
import io.github.mslxl.xmusic.common.addon.entity.*
import io.github.mslxl.xmusic.common.addon.processor.AlbumProcessor
import io.github.mslxl.xmusic.common.addon.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.addon.processor.SongProcessor
import io.github.mslxl.xmusic.common.addon.processor.ext.SearchableProcessor
import io.github.mslxl.xmusic.common.events.XMusicInitializationEvent
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.desktop.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.net.URL
import kotlin.coroutines.coroutineContext
import kotlin.random.Random


class FakeMusicSource : MusicSource {
    lateinit var core: XMusic
    override val name = "fake music source"
    override val id = "io.github.mslxl.xmusic.test.fakesource"
    override val information = FakeSongProcessor(this)
    override val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>> = mapOf("zh" to {
        listOf("fake music source" to "测试源 (JVM)")
    }, "en" to {
        listOf("fake music source" to "Test Source (JVM)")
    })

    fun createFakeData() = sequence {
        val logger = logger("FakeDataGenerator")
        repeat(10000) {
            val rand = Random.nextInt(1000, 10000).toString()
//            logger.info("yield $it - $rand")
            yield(EntitySongIndex(id = rand, source = id))
        }
    }

    override val discovery: Map<I18NKey, ExplorerProcessor<*, *>> = mapOf(
        "Test single 1" to FakeSongExplorer(this),
        "Test collection 1" to FakeCollectionExplorer(this),
        "Test album 1" to FakeSourceAlbumExplorer(this)
    )
    override val collection: CollectionProcessor = FakeSourceCollectionProcessor(this)
    override val album: AlbumProcessor = FakeSourceAlbumProcessor(this)

    @XMusicEventRegister
    fun init(event: XMusicInitializationEvent) {
        core = event.source
        core.i18n.insert(this)
    }
}

class FakeSourceAlbumProcessor(private val src: FakeMusicSource) : AlbumProcessor {
    override suspend fun getContent(entityAlbumIndex: EntityAlbumIndex): ReceiveChannel<EntitySongIndex> {
        return CoroutineScope(coroutineContext).produce {
            src.createFakeData().forEach {
                send(it)
            }
        }
    }

    override suspend fun getInformation(entityAlbumIndex: EntityAlbumIndex): EntityAlbum {
        return EntityAlbum(
            entityAlbumIndex,
            "name: ${entityAlbumIndex.uuid}", "fake data created by xmusic", "fake data generator"
        )
    }
}

class FakeSourceAlbumExplorer(private val src: MusicSource) : ExplorerProcessor<EntityAlbumIndex, EntityAlbum> {
    override suspend fun getExploredList(): ReceiveChannel<EntityAlbumIndex> {
        return CoroutineScope(coroutineContext).produce {
            repeat(10) {
                send(
                    EntityAlbumIndex(
                        id = Random.nextInt(1000, 9999).toString(),
                        sourceID = src.id
                    )
                )
            }
        }
    }

    override suspend fun getExploredDetail(index: EntityAlbumIndex): ReceiveChannel<EntityAlbum> {
        return CoroutineScope(coroutineContext).produce {
            send(
                EntityAlbum(
                    index = index,
                    name = "test: ${index.uuid}",
                    desc = "desc",
                    creator = "create"
                )
            )
        }
    }
}

class FakeSourceCollectionProcessor(val src: FakeMusicSource) : CollectionProcessor {
    override suspend fun getAllCollection(): ReceiveChannel<EntityCollectionIndex> {
        return CoroutineScope(coroutineContext).produce {}
    }

    override suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection {
        return EntityCollection(
            entity,
            "tit: ${entity.id}",
            desc = "fake data created by xmusic tester",
            creator = "XMusicDataGenerator"
        )
    }

    override suspend fun getContent(entity: EntityCollectionIndex): ReceiveChannel<EntitySongIndex> {
        return CoroutineScope(coroutineContext).produce {
            src.createFakeData().forEach {
                send(it)
            }
        }
    }
}

class FakeSongExplorer(private val src: FakeMusicSource) : ExplorerProcessor<EntitySongIndex, EntitySong> {

    override suspend fun getExploredList(): ReceiveChannel<EntitySongIndex> {
        return CoroutineScope(coroutineContext).produce {
            src.createFakeData().forEach {
                send(it)
            }
        }
    }

    override suspend fun getExploredDetail(index: EntitySongIndex): ReceiveChannel<EntitySong> {
        return CoroutineScope(coroutineContext).produce {
            send(
                EntitySong(index = index, id = index.id, title = "tit: ${index.uuid}", singer = "fake")
            )
        }
    }
}

class FakeCollectionExplorer(private val src: FakeMusicSource) :
    ExplorerProcessor<EntityCollectionIndex, EntityCollection> {
    override suspend fun getExploredList(): ReceiveChannel<EntityCollectionIndex> {
        return CoroutineScope(coroutineContext).produce {
            send(EntityCollectionIndex(Random.nextInt(1000, 9999).toString(), src.id))
        }
    }

    override suspend fun getExploredDetail(index: EntityCollectionIndex): ReceiveChannel<EntityCollection> {
        return CoroutineScope(coroutineContext).produce {
            send(src.collection.getDetail(index))
        }
    }
}


class FakeSongProcessor(private val src: FakeMusicSource) : SongProcessor,
    SearchableProcessor<EntitySongIndex, EntitySong> {
    override suspend fun getDetail(entitySongPreview: EntitySongIndex): List<EntitySong> {
        return listOf(
            EntitySong(
                index = entitySongPreview,
                id = entitySongPreview.id,
                title = "tit: ${entitySongPreview.uuid}",
                singer = "fake"
            )
        )
    }

    override suspend fun getURL(info: EntitySong, option: String): URL {
        return URL("debug", this::class.simpleName, info.toString())
    }

    override suspend fun search(title: String): Sequence<EntitySongIndex> {
        return src.createFakeData()
    }
}

fun main() {
    AddonsMan.register(FakeMusicSource::class)
    App.main(emptyArray())
}