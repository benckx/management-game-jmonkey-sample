package be.encelade.bricks;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class GameEngineImpl extends SimpleApplication implements GraphicEngine {

    public static void main(String[] args) {
        GameEngineImpl app = new GameEngineImpl();
        // app.start(JmeContext.Type.Display);
        app.start();
    }

    private static final boolean displayInfo = true;

    private boolean isTiltedY;

    private static final int moveSpeed = 9;
    private static final float bottomMargin = 0.10f;
    private static final float topMargin = 1 - bottomMargin;

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("EEE. yyyy - MM - dd");
    private BitmapText dateHUD;

    private Geometry floor;
    private Map<String, Geometry> myBoxes = new HashMap<>();
    private CameraNode cameraNode;

    private LogicEngine logicEngine;

    @Override
    public void simpleInitApp() {
        logicEngine = new LogicEngine();
        logicEngine.setGraphicEngine(this);

        setDisplayFps(displayInfo);
        setDisplayStatView(displayInfo);

        inputManager.setCursorVisible(true);

        makeDateGUI();
        makeFlatFloor();

        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Create", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Destroy", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        inputManager.addMapping("WHEEL_UP", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("WHEEL_DOWN", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addListener(analogListener, "Left", "Right", "Up", "Down", "Create", "Destroy", "WHEEL_UP", "WHEEL_DOWN");

        flyCam.setEnabled(false);

        System.out.println("cam location: " + cam.getLocation());
        System.out.println("cam direction: " + cam.getDirection());

        cameraNode = new CameraNode("cameraNode", cam);
        rootNode.attachChild(cameraNode);

//      setPseudoIsometricMode();
//      setTopViewMode();
//      setLeanedMode();
        setIsometricMode();
    }

    private void setTopViewMode() {
        cameraNode.move(0, 10, 0);
        cameraNode.rotate(FastMath.HALF_PI, 0, 0);
        isTiltedY = false;
    }

    private void setLeanedMode() {
        cameraNode.move(0, 9, 0);
        cameraNode.rotate(0.7f, 0, 0);
        isTiltedY = false;
    }

    private void setPseudoIsometricMode() {
        cameraNode.move(0, 7, 0);
        cameraNode.rotate(0, 0.6f, 0);
        cameraNode.rotate(0.6f, 0, 0);
        isTiltedY = true;
    }

    private void setIsometricMode() {
        cameraNode.move(0, 7, 0);
        cameraNode.rotate(0, FastMath.QUARTER_PI, 0);
        cameraNode.rotate(FastMath.QUARTER_PI, 0, 0);
        isTiltedY = true;
    }

    private void makeDateGUI() {
        dateHUD = new BitmapText(guiFont, false);
        dateHUD.setSize(guiFont.getCharSet().getRenderedSize());
        dateHUD.setColor(ColorRGBA.White);
        dateHUD.setLocalTranslation(10, settings.getHeight() - dateHUD.getSize() / 2, 0);
        guiNode.attachChild(dateHUD);
        updateDate(logicEngine.getDate());
    }

    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            float delta = value * speed * moveSpeed;

            switch (name) {
                case "Left":
                    moveLeft(delta);
                    break;
                case "Right":
                    moveRight(delta);
                    break;
                case "Up":
                    moveUp(delta);
                    break;
                case "Down":
                    moveDown(delta);
                    break;
                case "Create":
                    int i = 1;
                    for (CollisionResult collisionResult : getCursorCollisions()) {
                        // (For each “hit”, we know distance, impact point, geometry.)
                        float dist = collisionResult.getDistance();
                        Vector3f pt = collisionResult.getContactPoint();
                        String target = collisionResult.getGeometry().getName();
                        System.out.println("Selection #" + (i++) + ": " + target + " at " + pt + ", " + dist + " WU away.");

                        if (target.equals("floor")) {
                            float xDelta = (float) (Math.floor(pt.x));
                            float zDelta = (float) (Math.floor(pt.z));
                            String key = "myBox_" + (int) xDelta + "_" + (int) zDelta;

                            if (!myBoxes.containsKey(key)) {
                                Geometry box = makeBox(xDelta, zDelta);
                                myBoxes.put(key, box);
                                rootNode.attachChild(box);
                            }

                            break;
                        }
                    }
                    break;
                case "Destroy":
                    for (CollisionResult collisionResult : getCursorCollisions()) {
                        if (collisionResult.getGeometry().getName().startsWith("myBox_")) {
                            Vector3f pt = collisionResult.getContactPoint();
                            float xDelta = (float) (Math.floor(pt.x));
                            float zDelta = (float) (Math.floor(pt.z));
                            String key = "myBox_" + (int) xDelta + "_" + (int) zDelta;

                            if (myBoxes.containsKey(key)) {
                                myBoxes.remove(key);
                                rootNode.detachChildNamed(key);
                            }

                            break;
                        }
                    }
                    break;
                case "WHEEL_UP":
                    moveUp(speed);
                    cameraNode.move(0, -speed, 0);
                    break;
                case "WHEEL_DOWN":
                    moveDown(speed);
                    cameraNode.move(0, speed, 0);
                    break;
                default:
                    System.out.println("not treated: " + name);
                    break;
            }
        }
    };

    private float getSideDelta(float delta) {
        return isTiltedY ? delta * 0.8f : 0f;
    }

    private void moveLeft(float delta) {
        cameraNode.move(delta, 0, -getSideDelta(delta));
    }

    private void moveRight(float delta) {
        cameraNode.move(-delta, 0, getSideDelta(delta));
    }

    private void moveUp(float delta) {
        cameraNode.move(getSideDelta(delta), 0, delta);
    }

    private void moveDown(float delta) {
        cameraNode.move(-getSideDelta(delta), 0, -delta);
    }

    private CollisionResults getCursorCollisions() {
        CollisionResults results = new CollisionResults();

        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);

        // Collect intersections between ray and all nodes in results list.
        rootNode.collideWith(ray, results);

        return results;
    }

    private Geometry makeBox(float xDelta, float zDelta) {
        Box box = new Box(0.5f, 0.25f, 0.5f);
        String key = "myBox_" + (int) xDelta + "_" + (int) zDelta;
        Geometry geometry = new Geometry(key, box);
        geometry.move(xDelta + 0.5f, box.getYExtent(), zDelta + 0.5f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geometry.setMaterial(mat);
        return geometry;
    }

    private void makeBoxFloor() {
        floor = new Geometry("floor", new Box(5, 0.01f, 5));
        floor.move(0, 0, 0);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        floor.setMaterial(mat);
        rootNode.attachChild(floor);
    }

    private void makeFlatFloor() {
        Mesh mesh = new Mesh();

        // Vertex positions in space
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-5, -5, 0);
        vertices[1] = new Vector3f(5, -5, 0);
        vertices[2] = new Vector3f(-5, 5, 0);
        vertices[3] = new Vector3f(5, 5, 0);

        // Texture coordinates
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);

        // Indexes. We define the order in which mesh should be constructed
        short[] indexes = {2, 0, 1, 1, 3, 2};

        // Setting buffers
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createShortBuffer(indexes));
        mesh.updateBound();

        Geometry geometry = new Geometry("floor", mesh);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setBoolean("VertexColor", true);

        //We have 4 vertices and 4 color values for each of them.
        //If you have more vertices, you need 'new float[yourVertexCount * 4]' here!
        float[] colorArray = new float[4 * texCoord.length];
        int colorIndex = 0;

        //Set custom RGBA value for each Vertex. Values range from 0.0f to 1.0f
        for (int i = 0; i < texCoord.length; i++) {
            colorArray[colorIndex++] = 0.1f + (.2f * i); // Red value (is increased by .2 on each next vertex here)
            colorArray[colorIndex++] = 0.9f - (0.2f * i); // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 0.5f; // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 1.0f; // Alpha value (no transparency set here)
        }
        // Set the color buffer
        mesh.setBuffer(VertexBuffer.Type.Color, 4, colorArray);
        geometry.setMaterial(material);
        geometry.rotate(-FastMath.HALF_PI, 0, 0);

        rootNode.attachChild(geometry);
    }

    @Override
    public void simpleUpdate(float tpf) {
        logicEngine.addTpf(tpf);

        Vector2f click2d = inputManager.getCursorPosition();

        if (click2d.getX() < settings.getWidth() * bottomMargin) {
            moveLeft(tpf * moveSpeed);
        } else if (click2d.getX() > settings.getWidth() * topMargin) {
            moveRight(tpf * moveSpeed);
        }

        if (click2d.getY() < settings.getHeight() * bottomMargin) {
            moveDown(tpf * moveSpeed);
        } else if (click2d.getY() > settings.getHeight() * topMargin) {
            moveUp(tpf * moveSpeed);
        }
    }

    @Override
    public void updateDate(LocalDateTime date) {
        dateHUD.setText(dateFormatter.print(date));
    }

}