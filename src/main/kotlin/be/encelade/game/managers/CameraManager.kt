package be.encelade.game.managers

import be.encelade.game.managers.CameraManager.ViewMode.*
import com.jme3.app.SimpleApplication
import com.jme3.math.FastMath
import com.jme3.math.FastMath.PI
import com.jme3.math.FastMath.QUARTER_PI
import com.jme3.math.Vector3f
import com.jme3.scene.CameraNode

class CameraManager(app: SimpleApplication, val mouseManager: MouseManager, var viewMode: ViewMode) {

    private val cameraNode = CameraNode("cameraNode", app.camera)

    init {
        val rotation = rotationFor(viewMode)
        cameraNode.rotate(rotation.x, rotation.y, rotation.z)
        cameraNode.move(baseLocationFor(viewMode))
        app.rootNode.attachChild(cameraNode)
    }

    fun switchView() {
        val currentAngle = rotationFor(viewMode)
        cameraNode.rotate(-currentAngle.x, -currentAngle.y, -currentAngle.z)

        val newMode = viewMode.next()
        val newAngle = rotationFor(newMode)
        cameraNode.rotate(newAngle.x, newAngle.y, newAngle.z)
    }

    fun simpleUpdate(tpf: Float) {
        if (mouseManager.rightClickPressed && mouseManager.isCursorMoving()) {
            rightClickMovement(tpf)
        }
    }

    private fun rightClickMovement(tpf: Float) {
        // speed is proportional by Z axis (i.e. by distance from the floor),
        // so we move faster as we are zoomed out
        val movementSpeed = CAMERA_SPEED * cameraNode.camera.location.z

        val cameraMovement = when (viewMode) {
            TOP_VIEW, SIDE_VIEW -> {
                val deltaX = -mouseManager.deltaX * movementSpeed * tpf
                val deltaY = -mouseManager.deltaY * movementSpeed * tpf

                Vector3f(deltaX, deltaY, 0f)
            }
            ISO_VIEW -> {
                val deltaX = -(mouseManager.deltaX + mouseManager.deltaY) * movementSpeed * tpf
                val deltaY = -(mouseManager.deltaY - mouseManager.deltaX) * movementSpeed * tpf

                Vector3f(deltaX, deltaY, 0f)
            }
        }

        cameraNode.move(cameraMovement)
    }

    fun rotationFor(viewMode: ViewMode): Vector3f {
        return when (viewMode) {
            TOP_VIEW -> Vector3f(PI, 0f, PI)
            SIDE_VIEW -> Vector3f(PI * 0.75f, 0f, PI)
            ISO_VIEW -> Vector3f(QUARTER_PI * 3f, 0f, QUARTER_PI * 3f)
        }
    }

    fun cameraZoom(value: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = value * ZOOM_SPEED * currentZ
        val targetZ = currentZ + deltaZ

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            val cameraMovement = when (viewMode) {
                TOP_VIEW -> Vector3f(0f, 0f, deltaZ)
                SIDE_VIEW -> Vector3f(0f, -deltaZ / 2, deltaZ / 2)
                ISO_VIEW -> Vector3f(-deltaZ / 2, -deltaZ / 2, deltaZ)
            }

            cameraNode.move(cameraMovement)
        }
    }

    enum class ViewMode {
        TOP_VIEW, SIDE_VIEW, ISO_VIEW;

        fun next(): ViewMode {
            val idx =
                    if (ordinal == values().size - 1) {
                        0
                    } else {
                        ordinal + 1
                    }

            return values()[idx]
        }
    }

    companion object {

        const val CAMERA_SPEED = 0.02f
        const val ZOOM_SPEED = 2

        const val MIN_Z = 2
        const val MAX_Z = 40

        private fun baseLocationFor(viewMode: ViewMode): Vector3f {
            return when (viewMode) {
                TOP_VIEW -> Vector3f(0f, 0f, 15f)
                SIDE_VIEW -> Vector3f(0f, -15f, 15f)
                ISO_VIEW -> Vector3f(-10f, -10f, 15f)
            }
        }
    }
}