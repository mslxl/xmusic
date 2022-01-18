import io.github.mslxl.xmusic.common.util.controller
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ByteInputStreamTester {
    private fun getStream() = ByteInputStreamTester::class.java.getResourceAsStream("/ByteInputStreamTestText.txt")!!

    @Test
    fun `Test asString`() {
        getStream().controller().use {
            Assertions.assertEquals("ID3", it.read(3).asString())
        }
    }

    @Test
    fun `Test asByteHex`() {
        getStream().controller().use {
            Assertions.assertEquals("494433", it.read(3).asByteHex())
        }
    }

    @Test
    fun `Test asByteDec`() {
        getStream().controller().use {
            Assertions.assertEquals(0x494433, it.read(3).asByteDec())
        }
    }

    @Test
    fun `Test skip`() {
        getStream().controller().use {
            it.skip(3)
            Assertions.assertEquals("HELLOworld", it.read(10).asString())
            Assertions.assertEquals(0x3131, it.read(2).asByteDec())

        }
    }

    @Test
    fun `Test readUtil`() {
        getStream().controller().use {
            val fst = it.readUntil(5) { data ->
                data.asString() != "HELLO"
            }
            Assertions.assertEquals("ID3", fst.asString())
            Assertions.assertEquals("HELLO", it.read(5).asString())

            val snd = it.readUntil(1) { data ->
                data.asString() != "1"
            }
            Assertions.assertEquals(snd.asString(), "world")
            Assertions.assertEquals("114514", it.read(6).asString())
            Assertions.assertThrows(Exception::class.java) {
                it.read(1)
            }
        }

        getStream().controller().use {
            val empty = it.readUntil(2) { ahead ->
                ahead.asString() != "ID"
            }
            Assertions.assertEquals(0, empty.bytes.size)
            val fst = it.read(3)
            Assertions.assertEquals("ID3", fst.asString())
            val hello = it.readUntil(5) { ahead ->
                ahead.asString() != "world"
            }
            Assertions.assertEquals("HELLO", hello.asString())
            it.skip(3)
            val ld1 = it.read(3)
            Assertions.assertEquals("ld1", ld1.asString())

        }

    }


}