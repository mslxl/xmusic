package io.github.mslxl.xmusic.common.platform

import java.io.File

interface FileSystemEnv {
    val config: File
    val data: File
    val cache: File
}