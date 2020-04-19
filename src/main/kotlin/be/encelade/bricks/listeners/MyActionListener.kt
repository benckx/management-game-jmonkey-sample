package be.encelade.bricks.listeners

import be.encelade.bricks.managers.MouseManager
import com.jme3.input.controls.ActionListener

class MyActionListener(val mouseManager: MouseManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        mouseManager.rightClickPressed = isPressed
    }


}