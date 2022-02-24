import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.common.net.NetworkHandle
import io.github.mslxl.xmusic.common.src.SourceLocalMusic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URL

class NetworkTester {
    companion object{
        @BeforeAll
        @JvmStatic
        fun init() {
            AddonsMan.register(SourceLocalMusic::class)
        }
    }


    @Test
    fun get() {
        val network = NetworkHandle.require(SourceLocalMusic.id)
        val text = network.get(URL("https://www.baidu.com")).entity.content.bufferedReader().readText()
        Assertions.assertNotEquals(text, "")
        println(text)

    }
}