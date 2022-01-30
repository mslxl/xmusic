import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.i18n.I18nKey
import io.github.mslxl.xmusic.common.logger
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.ExplorerProcessor
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import io.github.mslxl.xmusic.common.source.processor.ext.SearchableProcessor
import io.github.mslxl.xmusic.desktop.App
import java.net.URL
import kotlin.random.Random

class FakeMusicSource(override var core: XMusic) : MusicSource {
    override val name = "fake music source"
    override val information = FakeSongProcessor(this)
    override val discovery: Map<I18nKey, ExplorerProcessor<*, *>> =
        mapOf(
            "Test 1" to information
        )

    override fun acceptConfig(config: SourceConfig) {
        config.markType("field.1", "Field 01", SourceConfig.ItemType.TEXT)
        config.markType("field.2", "Field 02", SourceConfig.ItemType.PASSWORD)
    }
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
    App.core.addMusicSource(FakeMusicSource(App.core))
    App.main(emptyArray())

}