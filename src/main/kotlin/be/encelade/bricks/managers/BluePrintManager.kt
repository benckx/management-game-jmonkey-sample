package be.encelade.bricks.managers

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box

class BluePrintManager(val app: SimpleApplication) {

    private var enabled = false

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
        destroy()
    }

    fun updatePosition(x: Int, y: Int) {
        if (enabled) {
            destroy()
            app.rootNode.attachChild(makeBox(x.toFloat(), y.toFloat()))
        }
    }

    private fun destroy() {
        app.rootNode.detachChildNamed("blueprint")
    }

    private fun makeBox(xDelta: Float, yDelta: Float): Geometry {
        val box = Box(0.5f, 0.5f, 0.1f)
        val key = "blueprint"
        val geometry = Geometry(key, box)
        geometry.move(xDelta, yDelta, 0f)
        val mat = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Blue)
        geometry.material = mat
        return geometry
    }
}