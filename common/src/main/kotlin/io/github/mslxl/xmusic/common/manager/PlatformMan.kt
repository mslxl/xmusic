package io.github.mslxl.xmusic.common.manager

import io.github.mslxl.xmusic.common.platform.CacheIndexDBManager
import io.github.mslxl.xmusic.common.platform.FileSystemEnv

object PlatformMan {
    lateinit var fs: FileSystemEnv
        private set
    lateinit var cacheDB: CacheIndexDBManager
        private set

    operator fun invoke(fs: FileSystemEnv, cacheDB: CacheIndexDBManager) {
        this.fs = fs
        this.cacheDB = cacheDB
    }

}