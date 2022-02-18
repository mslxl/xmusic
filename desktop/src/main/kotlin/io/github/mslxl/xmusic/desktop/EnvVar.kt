package io.github.mslxl.xmusic.desktop

import java.io.File

object EnvVar {
    val userHome = System.getProperty("user.home") ?: "."
    val programDir = File(userHome, ".config/xmusic").apply {
        if (!exists()) {
            mkdirs()
        }
    }
}