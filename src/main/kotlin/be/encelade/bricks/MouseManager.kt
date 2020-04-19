package be.encelade.bricks

import com.jme3.app.SimpleApplication
import com.jme3.math.Vector2f

class MouseManager(val app: SimpleApplication) {

    private var previousCursorPosition: Vector2f? = null

    var speedX = 0f
    var speedY = 0f
    var rightClickPressed = false

    fun simpleUpdate() {
        updateCursorSpeed(app.inputManager.cursorPosition)
    }

    fun isCursorMoving() = speedX != 0f || speedY != 0f

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        if (previousCursorPosition == null) {
            previousCursorPosition = currentPosition
        } else {
            speedX = currentPosition.x - previousCursorPosition!!.x
            speedY = currentPosition.y - previousCursorPosition!!.y
            previousCursorPosition = Vector2f(currentPosition)
        }
    }

}