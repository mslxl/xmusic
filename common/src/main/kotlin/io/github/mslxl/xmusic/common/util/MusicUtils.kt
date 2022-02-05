package io.github.mslxl.xmusic.common.util

import io.github.mslxl.xmusic.common.fs.CacheIndexDBManager
import io.github.mslxl.xmusic.common.logger
import java.io.File

object MusicUtils {
    private val logger = this::class.logger
    val defaultCover: File by lazy {
        File.createTempFile("default_cover.tmp", "png").apply {
            logger.info("Extract default cover to $absolutePath")
            outputStream().use { output ->
                MusicUtils::class.java
                    .getResourceAsStream("/io/github/mslxl/xmusic/common/res/default_cover.png")!!
                    .copyTo(output)
            }
        }
    }

    fun getCoverFromMp3(file: File, cacheIndex: CacheIndexDBManager): File {
        return cacheIndex.getOrInit("${file.absolutePath}-music_util") { targetFile ->
            logger.info("Parse ID3V2 tag from ${file.absolutePath}")
            file.inputStream()
                .controller()
                .use {
                    if (it.read(3).asString() != "ID3") {
                        // Invalid ID3v2 tag
                        defaultCover.copyTo(targetFile)
                        return@use
                    }
                    it.skip(2) // skip ID3v2 version

                    val hasExtendHeader: Boolean = it.run {
                        read(1)
                            .asBytes()
                            .first()
                            .let { flags ->
                                val extendFlag = 0b01000000
                                flags.toInt() and extendFlag == extendFlag
                            }
                    }
                    logger.info("Have extended header: $hasExtendHeader")

                    val tagSize: Int = it.run {
                        read(4)
                            .asBytes()
                            .asSequence()
                            .map(Byte::toInt)
                            .mapIndexed { index, byte ->
                                byte shl (3 - index) * 7
                            }.reduce { acc, i ->
                                acc or i
                            }
                    }
                    logger.info("Tag size: $tagSize")
                    if (hasExtendHeader) {
                        val extendedHeaderSize = it.read(4).asInt()
                        it.skip(extendedHeaderSize.toLong())
                        logger.info("skip $tagSize bytes extended header")
                    }
                    var foundImage = false
                    // Search image in APIC frame overview
                    while (it.position < tagSize) {
                        val frameID = it.read(4).asString(Charsets.ISO_8859_1)
                        val size = it.read(4).asInt()
                        logger.info("Found frame id $frameID, which size is $size")
                        it.skip(2) // skip frame flags
                        if (frameID == "APIC") {
                            val startPos = it.position
                            logger.info("Parse APIC frame")
                            it.skip(1) // skip Text encoding

                            val mime = it.readUntil(1) { ahead ->
                                ahead.asByteDec() != 0x00
                            }.asString()
                            it.skip(1) // skip end flag 0x00
                            logger.info("MIME: $mime")

                            val pictureType = it.read(1).asByteDec()
                            //TODO use correct picture instead of first picture, it need I to find a special mp3 file to complete it

                            val desc = it.readUntil(1) { ahead ->
                                ahead.asByteDec() != 0x00
                            }
                            it.skip(1) // skip end flag of desciption
                            if (desc.bytes.isEmpty()) {
                                logger.info("No description")
                            } else {
                                logger.info("Description: $desc")
                            }
                            val endPos = it.position
                            val remainSize = (size - (endPos - startPos)).toInt()
                            targetFile.writeBytes(it.read(remainSize).asBytes())
                            foundImage = true
                            break
                        } else {
                            it.skip(size)
                        }
                    }
                    if (!foundImage) {
                        // image not found
                        // use default image
                        defaultCover.copyTo(targetFile)
                    }
                }
        }
    }
}