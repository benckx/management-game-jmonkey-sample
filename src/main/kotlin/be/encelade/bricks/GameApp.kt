package be.encelade.bricks

import com.jme3.app.SimpleApplication
import com.jme3.input.MouseInput.AXIS_WHEEL
import com.jme3.input.controls.AnalogListener
import com.jme3.input.controls.MouseAxisTrigger
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath
import com.jme3.scene.CameraNode
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box

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

        inputManager.addMapping("WHEEL_UP", MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping("WHEEL_DOWN", MouseAxisTrigger(AXIS_WHEEL, true))

        inputManager.addListener(MyAnalogListener(this), "WHEEL_UP", "WHEEL_DOWN")

        makeBoxFloor()
    }

    private class MyAnalogListener(val parent : GameApp) : AnalogListener {
        override fun onAnalog(name: String?, value: Float, tpf: Float) {
            val delta: Float = value * parent.speed * 10

            when(name) {
                "WHEEL_UP" -> parent.cameraNode.move(0f, -delta, 0f)
                "WHEEL_DOWN" -> parent.cameraNode.move(0f, delta, 0f)
                else -> println("Unknown $name")
            }
        }
    }

    private fun makeBoxFloor() {
        // replace Box by Surface
        val floor = Geometry("floor", Box(5f, 0.01f, 5f))
        floor.move(0f, 0f, 0f)
        val mat = Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Gray)
        floor.material = mat
        rootNode.attachChild(floor)
    }

    private fun enableTopViewMode() {
        cameraNode.move(0f, 15f, 0f)
        cameraNode.rotate(FastMath.HALF_PI, 0f, 0f)
    }
}