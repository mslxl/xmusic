import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import io.github.mslxl.xmusic.desktop.App

class FakeMusicSource : MusicSource {
    override val name: String = "fake music source"
    override val information: SongProcessor
        get() = TODO("Not yet implemented")

    override fun acceptConfig(config: SourceConfig) {
        config.markType("field.1", "Field 01", SourceConfig.ItemType.TEXT)
        config.markType("field.2", "Field 02", SourceConfig.ItemType.PASSWORD)
    }

}

fun main() {
    App.core.addMusicSource(FakeMusicSource())
    App.main(emptyArray())

}