package io.github.mslxl.xmusic.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

private val loggerMap = hashMapOf<String, Logger>()

val KClass<*>.logger: Logger
    get() =
        loggerMap.getOrPut(this.qualifiedName.toString()) {
            LoggerFactory.getLogger(this.qualifiedName.toString())
        }

fun logger(name: String): Logger {
    return loggerMap.getOrPut("name-$name") {
        LoggerFactory.getLogger(name)
    }
}