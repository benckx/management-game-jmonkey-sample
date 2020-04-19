package be.encelade.bricks.managers

import be.encelade.bricks.managers.CameraManager.ViewMode.ISO_VIEW
import be.encelade.bricks.managers.CameraManager.ViewMode.TOP_VIEW
import com.jme3.app.SimpleApplication
import com.jme3.math.FastMath
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.scene.CameraNode

class CameraManager(val app: SimpleApplication, val mouseManager: MouseManager, var viewMode: ViewMode) {

    private val cameraNode = CameraNode("cameraNode", app.camera)

    init {
        when (viewMode) {
            TOP_VIEW -> {
                cameraNode.move(0f, 0f, 15f)
                enableTopViewMode()
            }
            ISO_VIEW -> {
                cameraNode.move(-10f, -10f, 15f)
                enableIsoViewMode()
            }
        }

        app.rootNode.attachChild(cameraNode)
    }

    fun simpleUpdate(tpf: Float) {
        if (mouseManager.rightClickPressed && mouseManager.isCursorMoving()) {
            // speed is proportional by Z axis (i.e. by distance from the floor),
            // so we move faster as we are zoomed out
            val movementSpeed = CAMERA_SPEED * cameraNode.camera.location.z

            val cameraMovement = when (viewMode) {
                TOP_VIEW -> {
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
    }

    fun enableTopViewMode() {
        viewMode = TOP_VIEW
        resetRotation()
        cameraNode.camera.rotation = Quaternion()
        cameraNode.rotate(FastMath.PI, 0f, FastMath.PI)
    }

    fun enableIsoViewMode() {
        viewMode = ISO_VIEW
        resetRotation()
        cameraNode.rotate(FastMath.PI, 0f, FastMath.PI) // top view
        cameraNode.rotate(-FastMath.QUARTER_PI, 0f, FastMath.QUARTER_PI)
    }

    fun cameraZoom(value: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = value * ZOOM_SPEED * currentZ
        val targetZ = currentZ + deltaZ

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            val cameraMovement = when (viewMode) {
                TOP_VIEW -> Vector3f(0f, 0f, deltaZ)
                ISO_VIEW -> Vector3f(-deltaZ / 2, -deltaZ / 2, deltaZ)
            }

            cameraNode.move(cameraMovement)
        }
    }

    private fun resetRotation() {
        cameraNode.camera.rotation = Quaternion()
    }

    enum class ViewMode {
        TOP_VIEW, ISO_VIEW
    }

    companion object {

        const val CAMERA_SPEED = 0.02f
        const val ZOOM_SPEED = 2

        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}