package io.github.mslxl.xmusic.desktop.src

import io.github.mslxl.xmusic.common.config.SourceConfig
import io.github.mslxl.xmusic.common.entity.EntityCollection
import io.github.mslxl.xmusic.common.entity.EntitySong
import io.github.mslxl.xmusic.common.entity.EntitySongInfo
import io.github.mslxl.xmusic.common.source.MusicSource
import io.github.mslxl.xmusic.common.source.processor.CollectionProcessor
import io.github.mslxl.xmusic.common.source.processor.SongProcessor
import java.net.URL

class SourceLocalMusic : MusicSource {
    lateinit var config: SourceConfig

    override val name: String
        get() = "LocalMusic"
    override val information = object : SongProcessor {
        override suspend fun getInfo(entitySong: EntitySong): List<EntitySongInfo> {
            TODO("Not yet implemented")
        }

        override suspend fun getURL(entitySong: EntitySong, info: EntitySongInfo, option: String): URL {
            TODO("Not yet implemented")
        }
    }

    override val collection = object : CollectionProcessor {
        override suspend fun getAllCollection(): Sequence<EntityCollection> {
            TODO("Not yet implemented")
        }

        override suspend fun getContent(): Sequence<EntitySong> {
            TODO("Not yet implemented")
        }

    }

    override fun acceptConfig(config: SourceConfig) {
        config.markType("path", "Music Folder", SourceConfig.ItemType.TEXT)
        this.config = config
    }



}