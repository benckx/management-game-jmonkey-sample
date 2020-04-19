package be.encelade.game

import com.jme3.system.AppSettings

fun main() {
    val settings = AppSettings(true)
    settings.isFullscreen = false
    settings.setResolution(1280, 720)
    settings.put("VSync", true)
    settings.put("Samples", 4)
    settings.put("Title", "Game")

    val gameApp = GameApp()
    gameApp.isShowSettings = false
    gameApp.setSettings(settings)
    gameApp.start()
}
