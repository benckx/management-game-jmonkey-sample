package be.encelade.game

import be.encelade.game.inputlisteners.MyActionListener
import be.encelade.game.inputlisteners.MyAnalogListener
import be.encelade.game.managers.BluePrintManager
import be.encelade.game.managers.CameraManager
import be.encelade.game.managers.CameraManager.ViewMode.ISO_VIEW
import be.encelade.game.managers.CameraManager.ViewMode.TOP_VIEW
import be.encelade.game.managers.MouseManager
import com.jme3.app.SimpleApplication
import com.jme3.input.KeyInput.*
import com.jme3.input.MouseInput.AXIS_WHEEL
import com.jme3.input.MouseInput.BUTTON_RIGHT
import com.jme3.input.controls.KeyTrigger
import com.jme3.input.controls.MouseAxisTrigger
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box
import com.jme3.scene.shape.Sphere

class GameApp : SimpleApplication() {

    private lateinit var bluePrintManager: BluePrintManager
    private lateinit var mouseManager: MouseManager
    private lateinit var cameraManager: CameraManager

    override fun simpleInitApp() {
        setDisplayFps(true)
        setDisplayStatView(true)

        flyCam.setEnabled(false)
        inputManager.setCursorVisible(true)

        bluePrintManager = BluePrintManager(this)
        mouseManager = MouseManager(this, bluePrintManager)
        cameraManager = CameraManager(this, mouseManager, ISO_VIEW)

        val myAnalogListener = MyAnalogListener(cameraManager)
        val myActionListener = MyActionListener(bluePrintManager, mouseManager)

        inputManager.clearMappings()

        inputManager.addMapping("B", KeyTrigger(KEY_B)) // TODO
        inputManager.addMapping("V", KeyTrigger(KEY_V)) // TODO
        inputManager.addMapping(ESCAPE, KeyTrigger(KEY_ESCAPE))
        inputManager.addMapping(WHEEL_UP, MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping(WHEEL_DOWN, MouseAxisTrigger(AXIS_WHEEL, true))
        inputManager.addMapping(MOUSE_RIGHT_CLICK, MouseButtonTrigger(BUTTON_RIGHT))

        inputManager.addListener(myAnalogListener, WHEEL_UP, WHEEL_DOWN)
        inputManager.addListener(myActionListener, MOUSE_RIGHT_CLICK, "B", ESCAPE, "V")

        showOrigin()
        addFloor()
    }

    override fun simpleUpdate(tpf: Float) {
        cameraManager.simpleUpdate(tpf)
        mouseManager.simpleUpdate()
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
        val size = 5.5f
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
        const val ESCAPE = "ESCAPE"

    }
}