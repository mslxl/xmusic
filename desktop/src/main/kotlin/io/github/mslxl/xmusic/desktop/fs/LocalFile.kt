package io.github.mslxl.xmusic.desktop.fs

import io.github.mslxl.xmusic.common.platform.FileSystemEnv
import io.github.mslxl.xmusic.desktop.EnvVar
import java.io.File

class LocalFile : FileSystemEnv {
    override val config: File = EnvVar.programDir.resolve("config")
    override val data: File = EnvVar.programDir.resolve("data")
    override val cache: File = EnvVar.programDir.resolve("cache")
}