package io.github.mslxl.xmusic.desktop.ui.view.songdetail

import io.github.mslxl.xmusic.common.addon.entity.EntitySong
import io.github.mslxl.xmusic.desktop.App

class SongDetailController(val view: SongDetailView) {
    fun playSong(song: EntitySong) {
        App.core.playlist.add(song)
    }
}