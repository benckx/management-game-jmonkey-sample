package be.encelade.game.managers

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.scene.Geometry
import com.jme3.scene.shape.Box

class BluePrintManager(val app: SimpleApplication) {

    private var enabled = false
    private var position = Pair(0, 0)

    private val mat: Material

    init {
        mat = Material(app.assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
        mat.setColor("Color", ColorRGBA.Blue)
    }

    fun enable() {
        enabled = true
        destroy()
        create()
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
            println("blueprint: $position")
        }
    }

    private fun create() {
        app.rootNode.attachChild(makeBox())
    }

    private fun destroy() {
        app.rootNode.detachChildNamed(BLUEPRINT_BLOCK)
    }

    private fun posX() = position.first.toFloat()
    private fun posY() = position.second.toFloat()

    private fun makeBox(): Geometry {
        val geometry = Geometry(BLUEPRINT_BLOCK, Box(0.5f, 0.5f, 0.1f))
        geometry.move(posX(), posY(), 0.1f)
        geometry.material = mat
        return geometry
    }

    companion object {

        const val BLUEPRINT_BLOCK = "BLUEPRINT_BLOCK"

    }
}