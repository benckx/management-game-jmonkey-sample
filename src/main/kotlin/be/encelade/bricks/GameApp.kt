package be.encelade.bricks

import be.encelade.bricks.listeners.MyActionListener
import be.encelade.bricks.listeners.MyAnalogListener
import com.jme3.app.SimpleApplication
import com.jme3.input.MouseInput.AXIS_WHEEL
import com.jme3.input.MouseInput.BUTTON_RIGHT
import com.jme3.input.controls.MouseAxisTrigger
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.scene.shape.Sphere

class GameApp : SimpleApplication() {

    private lateinit var cameraManager: CameraManager

    override fun simpleInitApp() {
        setDisplayFps(true)
        setDisplayStatView(true)

        flyCam.setEnabled(false)
        inputManager.setCursorVisible(true)

        cameraManager = CameraManager(this)
        cameraManager.register()
        cameraManager.enableTopViewMode()

        inputManager.addMapping(WHEEL_UP, MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping(WHEEL_DOWN, MouseAxisTrigger(AXIS_WHEEL, true))
        inputManager.addMapping(MOUSE_RIGHT_CLICK, MouseButtonTrigger(BUTTON_RIGHT))

        inputManager.addListener(MyAnalogListener(cameraManager), WHEEL_UP, WHEEL_DOWN)
        inputManager.addListener(MyActionListener(cameraManager), MOUSE_RIGHT_CLICK)

        showOrigin()
        addFloor()
    }

    override fun simpleUpdate(tpf: Float) {
        cameraManager.simpleUpdate(tpf)
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

    companion object {

        const val WHEEL_UP = "WHEEL_UP"
        const val WHEEL_DOWN = "WHEEL_DOWN"
        const val MOUSE_RIGHT_CLICK = "MOUSE_RIGHT_CLICK"

    }
}