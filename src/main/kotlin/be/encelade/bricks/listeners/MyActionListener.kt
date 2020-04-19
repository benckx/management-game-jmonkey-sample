package be.encelade.bricks.listeners

import be.encelade.bricks.CursorManager
import com.jme3.input.controls.ActionListener

class MyActionListener(val cursorManager: CursorManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        cursorManager.rightClickPressed = isPressed
    }


}