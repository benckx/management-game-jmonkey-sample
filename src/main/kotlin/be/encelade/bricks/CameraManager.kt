package be.encelade.bricks

import com.jme3.app.SimpleApplication
import com.jme3.math.FastMath
import com.jme3.math.Vector2f
import com.jme3.scene.CameraNode

class CameraManager(val app: SimpleApplication) {

    private val cameraNode = CameraNode("cameraNode", app.camera)

    private var previousCursorPosition: Vector2f? = null
    private var cursorSpeedX = 0f
    private var cursorSpeedY = 0f

    var rightClickPressed = false

    fun register() = app.rootNode.attachChild(cameraNode)

    fun simpleUpdate(tpf: Float) {
        updateCursorSpeed(app.inputManager.cursorPosition)

        if (rightClickPressed && (cursorSpeedX != 0f || cursorSpeedY != 0f)) {
            val movementSpeed = BASE_SPEED_RATE * cameraNode.camera.location.z * tpf
            cameraNode.move(cursorSpeedX * movementSpeed, cursorSpeedY * movementSpeed, 0f)
        }
    }

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        if (previousCursorPosition == null) {
            previousCursorPosition = currentPosition
        } else {
            cursorSpeedX = currentPosition.x - previousCursorPosition!!.x
            cursorSpeedY = currentPosition.y - previousCursorPosition!!.y
            previousCursorPosition = Vector2f(currentPosition)
        }
    }

    fun enableTopViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI, 0f, 0f)
    }

    fun enableIsoViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI * 0.8f, 0f, FastMath.PI * 0.8f)
    }

    fun cameraZoom(delta: Float) {
        val currentZ = cameraNode.camera.location.z
        val targetZ = currentZ + delta

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            cameraNode.move(0f, 0f, delta)
        }
    }

    companion object {

        const val BASE_SPEED_RATE = (1 / 60f)

        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}