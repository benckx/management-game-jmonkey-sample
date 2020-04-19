package be.encelade.bricks.managers

import com.jme3.app.SimpleApplication
import com.jme3.math.FastMath
import com.jme3.scene.CameraNode

class CameraManager(val app: SimpleApplication, val mouseManager: MouseManager) {

    private val cameraNode = CameraNode("cameraNode", app.camera)

    fun register() = app.rootNode.attachChild(cameraNode)

    fun simpleUpdate(tpf: Float) {
        if (mouseManager.rightClickPressed && mouseManager.isCursorMoving()) {
            val movementSpeed = CAMERA_SPEED * cameraNode.camera.location.z
            cameraNode.move(-mouseManager.speedX * movementSpeed * tpf, -mouseManager.speedY * movementSpeed * tpf, 0f)
        }
    }

    fun enableTopViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI, 0f, 0f)
        cameraNode.rotate(0f, 0f, FastMath.PI)
    }

    fun enableIsoViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI * 0.8f, 0f, FastMath.PI * 0.8f)
    }

    fun cameraZoom(value: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = value * ZOOM_SPEED * currentZ
        val targetZ = currentZ + deltaZ

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            cameraNode.move(0f, 0f, deltaZ)
        }
    }

    companion object {

        const val CAMERA_SPEED = 0.02f
        const val ZOOM_SPEED = 2

        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}