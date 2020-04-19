package be.encelade.bricks.managers

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box

class BluePrintManager(val app: SimpleApplication) {

    private var enabled = false
    private var position = Pair(0, 0)

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
        destroy()
    }

    fun updatePosition(x: Int, y: Int) {
        position = Pair(x, y)

        if (enabled) {
            destroy()
            create()
        }
    }

    private fun create() {
        app.rootNode.attachChild(makeBox())
    }

    private fun destroy() {
        app.rootNode.detachChildNamed("blueprint")
    }

    private fun posX() = position.first.toFloat()
    private fun posY() = position.second.toFloat()

    private fun makeBox(): Geometry {
        val box = Box(0.5f, 0.5f, 0.1f)
        val key = "blueprint"
        val geometry = Geometry(key, box)
        geometry.move(posX(), posY(), 0f)
        val mat = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Blue)
        geometry.material = mat
        return geometry
    }
}