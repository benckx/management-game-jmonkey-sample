package be.encelade.bricks.listeners

import be.encelade.bricks.CameraManager
import be.encelade.bricks.GameApp.Companion.WHEEL_DOWN
import be.encelade.bricks.GameApp.Companion.WHEEL_UP
import com.jme3.input.controls.AnalogListener

class MyAnalogListener(val cameraManager: CameraManager) : AnalogListener {

    override fun onAnalog(name: String?, value: Float, tpf: Float) {
        when (name) {
            WHEEL_UP -> cameraManager.cameraZoom(-value * tpf)
            WHEEL_DOWN -> cameraManager.cameraZoom(value * tpf)
            else -> println("Unknown $name")
        }
    }
}
