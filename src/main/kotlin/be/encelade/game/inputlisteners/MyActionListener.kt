package be.encelade.game.inputlisteners

import be.encelade.game.GameApp.Companion.ESCAPE
import be.encelade.game.GameApp.Companion.MOUSE_RIGHT_CLICK
import be.encelade.game.managers.BluePrintManager
import be.encelade.game.managers.CameraManager
import be.encelade.game.managers.MouseManager
import com.jme3.input.controls.ActionListener

class MyActionListener(val bluePrintManager: BluePrintManager, val mouseManager: MouseManager, val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        if (name == MOUSE_RIGHT_CLICK) {
            mouseManager.rightClickPressed = isPressed
        } else if (isPressed) {
            when (name) {
                "B" -> bluePrintManager.enable()
                ESCAPE -> bluePrintManager.disable()
                "V" -> cameraManager.switchViewMode()
                else -> println("Unknown $name")
            }
        }
    }


}