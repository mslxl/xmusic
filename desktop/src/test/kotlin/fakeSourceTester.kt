import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.addon.MusicSource
import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.common.addon.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.addon.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.addon.processor.SongProcessor
import io.github.mslxl.xmusic.common.addon.processor.ext.SearchableProcessor
import io.github.mslxl.xmusic.common.i18n.I18NKey
import io.github.mslxl.xmusic.common.i18n.I18NLocalCode
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.desktop.App
import java.net.URL
import kotlin.random.Random

class FakeMusicSource(var core: XMusic) : MusicSource {
    override val name = "fake music source"
    override val id = "io.github.mslxl.xmusic.test.fakesource"
    override val information = FakeSongProcessor(this)
    override val i18n: Map<I18NLocalCode, () -> List<Pair<I18NKey, String>>> = mapOf(
        "zh" to {
            listOf(
                "fake music source" to "测试源 (JVM)"
            )
        },
        "en" to {
            listOf(
                "fake music source" to "Test Source (JVM)"
            )
        }

    )
    override val discovery: Map<I18NKey, ExplorerProcessor<*, *>> =
        mapOf(
            "Test 1" to information
        )

}


class FakeSongProcessor(private val src: FakeMusicSource) : SongProcessor,
        SearchableProcessor<EntitySongIndex, EntitySong>,
    ExplorerProcessor<EntitySongIndex, EntitySong> {
    companion object {
        private val logger = FakeSongProcessor::class.logger
    }

    fun createFakeData() = sequence {
        repeat(10000) {
            val rand = Random.nextInt(1000, 10000).toString()
            logger.info("yield $it - $rand")
            yield(
                EntitySongIndex(
                    id = rand,
                    source = "fake music source".hashCode().toString()
                )
            )
        }
    }

    override suspend fun getDetail(entitySongPreview: EntitySongIndex): Sequence<EntitySong> {
        return sequence {
            yield(
                EntitySong(
                    index = entitySongPreview,
                    id = entitySongPreview.id,
                    title = "tit: ${entitySongPreview.uuid}",
                    singer = "fake"
                )
            )
        }
    }

    override suspend fun getURL(info: EntitySong, option: String): URL {
        return URL("debug", this::class.simpleName, info.toString())
    }

    override suspend fun search(title: String): Sequence<EntitySongIndex> {
        return createFakeData()
    }

    override suspend fun getExploredList(): Sequence<EntitySongIndex> {
        return createFakeData()
    }

    override suspend fun getExploredDetail(index: EntitySongIndex): Sequence<EntitySong> {
        return sequence {
            yield(
                EntitySong(
                    index = index,
                    id = index.id,
                    title = "tit: ${index.uuid}",
                    singer = "fake"
                )
            )
        }
    }


}

fun main() {
    App.main(emptyArray())

}