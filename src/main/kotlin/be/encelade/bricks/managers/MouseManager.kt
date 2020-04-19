package be.encelade.bricks.managers

import com.jme3.app.SimpleApplication
import com.jme3.collision.CollisionResult
import com.jme3.collision.CollisionResults
import com.jme3.math.Ray
import com.jme3.math.Vector2f
import com.jme3.math.Vector3f
import kotlin.math.roundToInt

class MouseManager(val app: SimpleApplication, val bluePrintManager: BluePrintManager) {

    private var previousCursorPosition: Vector2f? = null

    var deltaX = 0f
    var deltaY = 0f
    var rightClickPressed = false

    var floorPosition = Pair(0, 0)

    fun simpleUpdate() {
        updateCursorSpeed(app.inputManager.cursorPosition)

        if (!rightClickPressed) {
            updateCursorPositionOnTheFloor()
        }
    }

    fun isCursorMoving() = deltaX != 0f || deltaY != 0f

    private fun updateCursorPositionOnTheFloor() {
        val floorCollision = getFloorCollision()
        if (floorCollision != null) {
            val wuX = floorCollision.contactPoint.x.roundToInt()
            val wuY = floorCollision.contactPoint.y.roundToInt()
            if (wuX != floorPosition.first || wuY != floorPosition.second) {
                // TODO: replace by listener system
//                println("change from $floorPosition to ($wuX, $wuY)")
                floorPosition = Pair(wuX, wuY)
                bluePrintManager.updatePosition(wuX, wuY)
            }
        }
    }

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        if (previousCursorPosition == null) {
            previousCursorPosition = currentPosition
        } else {
            deltaX = currentPosition.x - previousCursorPosition!!.x
            deltaY = currentPosition.y - previousCursorPosition!!.y
            previousCursorPosition = Vector2f(currentPosition)
        }
    }

    private fun getFloorCollision(): CollisionResult? {
        return getCursorCollisions().find { it.geometry.name == "floor" }
    }

    private fun getCursorCollisions(): CollisionResults {
        // Convert screen click to 3d position
        val click2d: Vector2f = app.inputManager.getCursorPosition()
        val click3d: Vector3f = app.camera.getWorldCoordinates(Vector2f(click2d.x, click2d.y), 0f).clone()
        val dir: Vector3f = app.camera.getWorldCoordinates(Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal()

        // Aim the ray from the clicked spot forwards.
        val ray = Ray(click3d, dir)

        // Collect intersections between ray and all nodes in results list.
        val results = CollisionResults()
        app.rootNode.collideWith(ray, results)
        return results
    }
}