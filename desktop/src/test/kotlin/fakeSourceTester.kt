import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.XMusicEventRegister
import io.github.mslxl.xmusic.common.addon.entity.EntityCollection
import io.github.mslxl.xmusic.common.addon.entity.EntityCollectionIndex
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
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
import java.net.URL
import kotlin.random.Random

private fun createFakeData() = sequence {
    val logger = logger("FakeDataGenerator")
    repeat(10000) {
        val rand = Random.nextInt(1000, 10000).toString()
        logger.info("yield $it - $rand")
        yield(EntitySongIndex(id = rand, source = "fake music source".hashCode().toString()))
    }
}

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
    override val discovery: Map<I18NKey, ExplorerProcessor<*, *>> = mapOf("Test single 1" to FakeSongExplorer(this), "Test collection 1" to FakeCollectionExplorer(this))
    override val collection: CollectionProcessor = FakeSourceCollectionProcessor()

    @XMusicEventRegister
    fun init(event: XMusicInitializationEvent) {
        core = event.source
        core.i18n.insert(this)
    }

}

class FakeSourceCollectionProcessor : CollectionProcessor {
    override suspend fun getAllCollection(): Sequence<EntityCollectionIndex> {
        return emptySequence()
    }

    override suspend fun getDetail(entity: EntityCollectionIndex): EntityCollection {
        return EntityCollection(entity, "tit: ${entity.id}", desc = "fake data created by xmusic tester", creator = "XMusicDataGenerator")
    }

    override suspend fun getContent(entity: EntityCollectionIndex): Sequence<EntitySongIndex> {
        return createFakeData()
    }
}

class FakeSongExplorer(private val src: FakeMusicSource) : ExplorerProcessor<EntitySongIndex, EntitySong> {

    override suspend fun getExploredList(): Sequence<EntitySongIndex> {
        return createFakeData()
    }

    override suspend fun getExploredDetail(index: EntitySongIndex): Sequence<EntitySong> {
        return sequence {
            yield(EntitySong(index = index, id = index.id, title = "tit: ${index.uuid}", singer = "fake"))
        }
    }
}

class FakeCollectionExplorer(private val src: FakeMusicSource) : ExplorerProcessor<EntityCollectionIndex, EntityCollection> {
    override suspend fun getExploredList(): Sequence<EntityCollectionIndex> {
        return sequence {
            repeat(5) {
                yield(EntityCollectionIndex(Random.nextInt(1000, 9999).toString(), src.id))
            }
        }
    }

    override suspend fun getExploredDetail(index: EntityCollectionIndex): Sequence<EntityCollection> {
        return sequenceOf(src.collection.getDetail(index))
    }
}


class FakeSongProcessor(private val src: FakeMusicSource) : SongProcessor, SearchableProcessor<EntitySongIndex, EntitySong> {
    override suspend fun getDetail(entitySongPreview: EntitySongIndex): Sequence<EntitySong> {
        return sequence {
            yield(EntitySong(index = entitySongPreview, id = entitySongPreview.id, title = "tit: ${entitySongPreview.uuid}", singer = "fake"))
        }
    }

    override suspend fun getURL(info: EntitySong, option: String): URL {
        return URL("debug", this::class.simpleName, info.toString())
    }

    override suspend fun search(title: String): Sequence<EntitySongIndex> {
        return createFakeData()
    }
}


fun main() {
    AddonsMan.register(FakeMusicSource::class)
    App.main(emptyArray())
}