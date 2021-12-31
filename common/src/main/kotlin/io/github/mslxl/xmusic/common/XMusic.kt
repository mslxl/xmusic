package io.github.mslxl.xmusic.common

import io.github.mslxl.xmusic.common.fs.FileSystem
import io.github.mslxl.xmusic.common.source.MusicSource

object XMusic {
    const val version = "0.0.1-alpha"

    lateinit var fs: FileSystem

    val sources: ArrayList<MusicSource> = arrayListOf()

}