package be.encelade.game

import com.jme3.system.AppSettings

fun main() {
    val settings = AppSettings(true)
    settings.isFullscreen = false
    settings.setResolution(1920, 1024)
    settings.put("VSync", true)
    settings.put("Samples", 4)
    settings.put("Title", "Bricks")

    val gameApp = GameApp()
    gameApp.isShowSettings = false
    gameApp.setSettings(settings)
    gameApp.start()
}
