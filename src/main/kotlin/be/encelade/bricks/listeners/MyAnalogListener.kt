package be.encelade.bricks.listeners

import be.encelade.bricks.GameApp.Companion.ESCAPE
import be.encelade.bricks.GameApp.Companion.WHEEL_DOWN
import be.encelade.bricks.GameApp.Companion.WHEEL_UP
import be.encelade.bricks.managers.BluePrintManager
import be.encelade.bricks.managers.CameraManager
import com.jme3.input.controls.AnalogListener

class MyAnalogListener(val bluePrintManager: BluePrintManager, val cameraManager: CameraManager) : AnalogListener {

    override fun onAnalog(name: String?, value: Float, tpf: Float) {
        when (name) {
            "B" -> bluePrintManager.enable()
            ESCAPE -> bluePrintManager.disable()
            WHEEL_UP -> cameraManager.cameraZoom(-value * tpf)
            WHEEL_DOWN -> cameraManager.cameraZoom(value * tpf)
            else -> println("Unknown $name")
        }
    }
}
