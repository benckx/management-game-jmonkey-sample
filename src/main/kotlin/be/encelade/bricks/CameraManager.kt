package be.encelade.bricks

import com.jme3.app.SimpleApplication
import com.jme3.math.FastMath
import com.jme3.math.Vector2f
import com.jme3.scene.CameraNode

class CameraManager(val app: SimpleApplication) {

    private val cameraNode = CameraNode("cameraNode", app.camera)

    private var oldPosition: Vector2f? = null
    private var deltaX = 0f
    private var deltaY = 0f

    var rightClickPressed = false

    fun register() = app.rootNode.attachChild(cameraNode)

    fun simpleUpdate(tpf: Float) {
        val currentPosition = Vector2f(app.inputManager.cursorPosition)

        if (oldPosition == null) {
            oldPosition = currentPosition
        } else {
            deltaX = currentPosition.x - oldPosition!!.x
            deltaY = currentPosition.y - oldPosition!!.y
            oldPosition = Vector2f(currentPosition)
        }

        if (rightClickPressed) {
            if (deltaX != 0f || deltaY != 0f) {
                println("${deltaX}, ${deltaY}")
                val rate = (1 / 70f) * cameraNode.camera.location.z * tpf
                cameraNode.move(deltaX * rate, deltaY * rate, 0f)
            }
        }
    }

    fun enableTopViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI, 0f, 0f)
    }

    fun cameraZoom(delta: Float) {
        val currentZ = cameraNode.camera.location.z
        val targetZ = currentZ + delta

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            cameraNode.move(0f, 0f, delta)
        }
    }

    companion object {

        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}