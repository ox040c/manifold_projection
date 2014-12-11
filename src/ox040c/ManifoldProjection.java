package ox040c;

/**
 * Created by Yuanzhang on 10/21/2014.
 */
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import utility.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class ManifoldProjection {

    private static Camera camera;
    private static float[] lightPosition = {-2.19f, 1.36f, 11.45f, 1f};
    private static int bunnyDisplayList;

    private static final String MODEL_LOCATION = "res/models/cow_vn.obj";

    public static void main(String[] args) {
        setUpDisplay();
        setUpDisplayLists();
        setUpCamera();
        setUpLighting();

        while (!Display.isCloseRequested()) {
            render();
            checkInput();
            Display.update();
            Display.sync(60);
        }
        cleanUp();
        System.exit(0);
    }

    private static void setUpDisplayLists() {
        Model m = null;
        try {
            m = OBJLoader.loadTexturedModel(new File(MODEL_LOCATION));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        bunnyDisplayList = OBJLoader.createTexturedDisplayList(m);
        glEnable(GL_TEXTURE_2D);
        m.getMaterials().get("cow_vn_mtl").texture.bind();
    }

    private static void checkInput() {
        camera.processMouse(1, 80, -80);
        camera.processKeyboard(16, 1, 1, 1);
        glLight(GL_LIGHT0, GL_POSITION, BufferTools.asFlippedFloatBuffer(lightPosition));
        if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
            lightPosition = new float[]{camera.x(), camera.y(), camera.z(), 1};
        }
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (Mouse.isButtonDown(1)) {
            Mouse.setGrabbed(false);
        }
    }

    private static void cleanUp() {
        glDeleteLists(bunnyDisplayList, 1);
        Display.destroy();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        camera.applyTranslations();

        glCallList(bunnyDisplayList);
    }

    private static void setUpLighting() {
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
        glLight(GL_LIGHT0, GL_POSITION, BufferTools.asFlippedFloatBuffer(new float[]{0, 0, 0, 1}));
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_DIFFUSE);
    }

    private static void setUpCamera() {
        camera = new EulerCamera.Builder().setAspectRatio((float) Display.getWidth() / Display.getHeight())
                .setPosition(-1.19f, 1.36f, 5.45f).setFieldOfView(70).build();
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
    }

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 800));
            Display.setVSyncEnabled(true);
            Display.setTitle("Happy CGing....");
            Display.create();
        } catch (LWJGLException e) {
            System.err.println("The display wasn't initialized correctly. :(");
            Display.destroy();
            System.exit(1);
        }
    }
}
