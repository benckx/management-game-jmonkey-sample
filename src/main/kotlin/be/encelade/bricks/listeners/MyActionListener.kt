package be.encelade.bricks.listeners

import be.encelade.bricks.CameraManager
import com.jme3.input.controls.ActionListener

class MyActionListener(val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        cameraManager.rightClickPressed = isPressed
    }


}