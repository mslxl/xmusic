import io.github.mslxl.xmusic.common.XMusic
import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongIndex
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import io.github.mslxl.xmusic.common.source.processor.ext.SearchableProcessor
import io.github.mslxl.xmusic.desktop.App
import java.net.URL
import kotlin.random.Random

class FakeMusicSource(override var core: XMusic) : MusicSource {
    override val name: String = "fake music source"
    override val information: SongProcessor = FakeSongProcessor(this)

    override fun acceptConfig(config: SourceConfig) {
        config.markType("field.1", "Field 01", SourceConfig.ItemType.TEXT)
        config.markType("field.2", "Field 02", SourceConfig.ItemType.PASSWORD)
    }


}

val FakeSongData = sequence<String> {
    repeat(10000) {
        yield(Random.nextInt(1000, 10000).toString())
    }
}


class FakeSongProcessor(private val src: FakeMusicSource) : SongProcessor, SearchableProcessor<EntitySongIndex> {

    override suspend fun getDetail(entitySongPreview: EntitySongIndex): Sequence<EntitySong> {
        return emptySequence()
    }

    override suspend fun getURL(info: EntitySong, option: String): URL {
        TODO()
    }

    override suspend fun search(title: String): Sequence<EntitySongIndex> {
        TODO()
    }

}

fun main() {
    App.core.addMusicSource(FakeMusicSource(App.core))
    App.main(emptyArray())

}