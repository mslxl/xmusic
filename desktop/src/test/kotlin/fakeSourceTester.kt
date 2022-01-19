import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import io.github.mslxl.xmusic.desktop.App
import java.net.URL

class FakeMusicSource : MusicSource {
    override val name: String = "fake music source"
    override val information: SongProcessor
        get() = object : SongProcessor {
            override suspend fun getInfo(entitySong: EntitySong): EntitySongInfo {
                TODO("Not yet implemented")
            }

            override suspend fun getURL(info: EntitySongInfo, option: String): URL {
                TODO("Not yet implemented")
            }

        }

    override fun acceptConfig(config: SourceConfig) {
        config.markType("field.1", "Field 01", SourceConfig.ItemType.TEXT)
        config.markType("field.2", "Field 02", SourceConfig.ItemType.PASSWORD)
    }

}

fun main() {
    App.core.addMusicSource(FakeMusicSource())
    App.main(emptyArray())

}