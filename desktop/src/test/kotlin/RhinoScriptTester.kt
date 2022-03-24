import io.github.mslxl.xmusic.common.addon.rhino.RhinoMusicSource
import io.github.mslxl.xmusic.common.manager.AddonsMan
import io.github.mslxl.xmusic.desktop.App

class RhinoScriptTester

fun main() {
    val text =
        RhinoScriptTester::class.java.classLoader.getResourceAsStream("RhinoScript.js")!!.bufferedReader().readText()
    val src = RhinoMusicSource("test", text)
    AddonsMan.register(src)
    App.main(emptyArray())

}
