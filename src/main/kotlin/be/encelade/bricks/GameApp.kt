package be.encelade.bricks

import com.jme3.app.SimpleApplication
import com.jme3.input.MouseInput
import com.jme3.input.MouseInput.AXIS_WHEEL
import com.jme3.input.controls.ActionListener
import com.jme3.input.controls.AnalogListener
import com.jme3.input.controls.MouseAxisTrigger
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath
import com.jme3.math.Vector2f
import com.jme3.scene.CameraNode
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.scene.shape.Sphere

class GameApp : SimpleApplication() {

    private lateinit var cameraNode: CameraNode

    override fun simpleInitApp() {
        setDisplayFps(true)
        setDisplayStatView(true)

        flyCam.setEnabled(false)
        inputManager.setCursorVisible(true)

        cameraNode = CameraNode("cameraNode", cam)
        rootNode.attachChild(cameraNode)
        enableTopViewMode()
//        enableIsoViewMode()

        inputManager.addMapping("WHEEL_UP", MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping("WHEEL_DOWN", MouseAxisTrigger(AXIS_WHEEL, true))
        inputManager.addMapping("MOUSE_RIGHT_CLICK", MouseButtonTrigger(MouseInput.BUTTON_RIGHT))

        inputManager.addListener(MyAnalogListener(this), "WHEEL_UP", "WHEEL_DOWN")
        inputManager.addListener(MyActionListener(this), "MOUSE_RIGHT_CLICK")

        showOrigin()
        addFloor()
    }

    private fun cameraZoom(value: Float) {
        val delta = value
        val currentZ = cameraNode.camera.location.z
        val targetZ = currentZ + delta

        println("----")
        println("delta: $delta")
        println("currentZ: $currentZ")
        println("targetZ: $targetZ")

        if (targetZ > MIN_Z && targetZ < MAX_Z) {
            cameraNode.move(0f, 0f, delta)
        } else {
            println("not allowed")
        }
    }

    private var oldPosition: Vector2f? = null
    private var deltaX = 0f
    private var deltaY = 0f
    private var rightClickPressed = false

    override fun simpleUpdate(tpf: Float) {
        val currentPosition = Vector2f(inputManager.cursorPosition)

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

    private class MyAnalogListener(val parent: GameApp) : AnalogListener {
        override fun onAnalog(name: String?, value: Float, tpf: Float) {
            when (name) {
                "WHEEL_UP" -> parent.cameraZoom(-value)
                "WHEEL_DOWN" -> parent.cameraZoom(value)
                else -> println("Unknown $name")
            }
        }
    }

    private class MyActionListener(val parent: GameApp) : ActionListener {

        override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
            println("is pressed: $isPressed")
            parent.rightClickPressed = isPressed
        }

    }

    private fun showOrigin() {
        val origin = Geometry("origin", Sphere(10, 10, 0.1f))
        val material = Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        material.setColor("Color", ColorRGBA.Red)
        origin.material = material
        rootNode.attachChild(origin)
    }

    private fun addFloor() {
        // replace Box by Surface
        val size = 5f
        val floor = Geometry("floor", Box(size, size, 0.01f))
        val mat = Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Gray)
        floor.material = mat
        rootNode.attachChild(floor)
    }

    private fun enableTopViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI, 0f, 0f)
    }

    fun enableIsoViewMode() {
        cameraNode.move(0f, 0f, 15f)
        cameraNode.rotate(FastMath.PI * 0.8f, 0f, FastMath.PI * 0.8f)
    }

    companion object {

        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}