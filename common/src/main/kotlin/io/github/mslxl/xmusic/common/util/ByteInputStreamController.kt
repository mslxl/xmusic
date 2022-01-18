package io.github.mslxl.xmusic.common.util

import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

@Suppress("NOTHING_TO_INLINE")
@JvmInline
value class BytesBlock(val bytes: ByteArray) {
    inline fun asBytes() = bytes

    inline fun asInt(): Int {
        val len = bytes.size
        return bytes.asSequence().map { it.toInt() and 0xff }.reduceIndexed { index, acc, i ->
            val shift = (len - 1 - index) * 8
            acc + (i shl shift)
        }
    }

    inline fun requireByteSize(require: Int) {
        if (bytes.size != require) {
            error("Bytes size does not satisfied requirement")
        }
    }

    inline fun asI32(): Int = this.run {
        requireByteSize(4)
        asInt()
    }

    inline fun asString(charset: Charset = Charsets.UTF_8): String {
        return bytes.toString(charset)
    }

    inline fun asByteHex(): String {
        return bytes.asSequence().map(Byte::toInt).map { it.toString(16) }.joinToString(separator = "")
    }

    inline fun asByteDec(): Int {
        return bytes.asSequence().map(Byte::toInt).reduce { acc, i ->
            (acc shl 8) or i
        }
    }

    inline fun slice(range: IntRange): BytesBlock {
        return BytesBlock(bytes.slice(range).toByteArray())
    }

}

class ByteInputStreamController(private val inputStream: InputStream) : AutoCloseable {
    var position: Long = 0
        private set
        get() = field - buffer.size
    private val buffer = LinkedList<Byte>()

    fun read(len: Int): BytesBlock {
        if (buffer.isEmpty()) {
            val bytes = ByteArray(len)
            if (inputStream.read(bytes) < len) {
                error("file length does not satisfied")
            }
            position += len
            return BytesBlock(bytes)
        } else if (buffer.size >= len) {
            val bytes = ByteArray(len) {
                buffer.poll()
            }
            return BytesBlock(bytes)
        } else {
            val bufferLen = buffer.size
            val bytes = ByteArray(len) {
                if (it <= buffer.size) {
                    buffer.poll()
                } else {
                    0
                }
            }
            val readLen = inputStream.read(bytes, bufferLen, len - bufferLen)
            if (readLen + bufferLen < len) {
                error("file length does not satisfied")
            }
            position += readLen
            return BytesBlock(bytes)
        }
    }

    fun readUtil(aheadSize: Int = 1, estimateSize: Int = 1024, predicate: (BytesBlock) -> Boolean): BytesBlock {
        check(aheadSize > 0)

        val array = ArrayList<Byte>(estimateSize)


        val firstRead = read(aheadSize)
        if (aheadSize == 1) {
            var curData = firstRead
            while (predicate.invoke(curData)) {
                array.add(curData.bytes.first())
                curData = read(1)
            }
            buffer.offer(curData.bytes.first())
        } else {
            val testBlock = LinkedList(firstRead.bytes.toList())
            while (predicate.invoke(BytesBlock(testBlock.toByteArray()))) {
                array.add(testBlock.poll())
                testBlock.offer(read(1).bytes.first())
            }
            buffer.addAll(testBlock)
        }
        return BytesBlock(array.toByteArray())
    }

    fun skip(len: Long) {
        var len = len
        if (buffer.isNotEmpty()) {
            while (len != 0L && buffer.isNotEmpty()) {
                buffer.poll()
                len--
            }
        }
        if (len != 0L) {
            inputStream.skip(len)
            position += len
        }
    }


    fun skip(len: Int) {
        skip(len.toLong())
    }

    override fun close() {
        inputStream.close()
        buffer.clear()
    }
}

fun InputStream.controller(): ByteInputStreamController {
    return ByteInputStreamController(this)
}