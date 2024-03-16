package finalproject;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

public class Programming {

    public static void main(String[] args) {
        Programming viewer = new Programming();
        viewer.start();
    }

    private FPCameraController camera;
    private DisplayMode displayMode;

    public void start() {
        try {
            createWindow();
            initGL();
            camera = new FPCameraController(0, 0, 0);
            camera.gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Simple 3D Viewer");
        Display.create();
    }

    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private class FPCameraController {

        private Vector3f position = null;
        private float yaw = 0.0f;
        private float pitch = 0.0f;

        public FPCameraController(float x, float y, float z) {
            position = new Vector3f(x, y, z);
        }

        public void yaw(float amount) {
            yaw += amount;
        }

        public void pitch(float amount) {
            pitch += amount;
            if (pitch < -90) {
                pitch = -90;
            } else if (pitch > 90) {
                pitch = 90;
            }
        }

        public void walkForward(float distance) {
            float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
            float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
            position.x += deltaX;
            position.z -= deltaZ;
        }

        public void walkBackwards(float distance) {
            float deltaX = distance * (float) Math.sin(Math.toRadians(yaw));
            float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw));
            position.x -= deltaX;
            position.z += deltaZ;
        }

        public void strafeLeft(float distance) {
            float deltaX = distance * (float) Math.sin(Math.toRadians(yaw - 90));
            float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw - 90));
            position.x -= deltaX;
            position.z += deltaZ;
        }

        public void strafeRight(float distance) {
            float deltaX = distance * (float) Math.sin(Math.toRadians(yaw + 90));
            float deltaZ = distance * (float) Math.cos(Math.toRadians(yaw + 90));
            position.x -= deltaX;
            position.z += deltaZ;
        }

        public void moveUp(float distance) {
            position.y -= distance;
        }

        public void moveDown(float distance) {
            position.y += distance;
        }

        public void gameLoop() {
            while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                Keyboard.poll();
                processInput();
                updateView();

                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                render();

                Display.update();
                Display.sync(60);
            }
        }

        private void render() {
            try {
                glBegin(GL_QUADS);

                // Top face (blue)
                glColor3f(0.0f, 0.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);

                // Bottom face (orange)
                glColor3f(1.0f, 0.5f, 0.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);

                // Front face (red)
                glColor3f(1.0f, 0.0f, 0.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);

                // Back face (green)
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);

                // Left face (yellow)
                glColor3f(1.0f, 1.0f, 0.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, -1.0f);
                glVertex3f(-1.0f, -1.0f, 1.0f);

                // Right face (magenta)
                glColor3f(1.0f, 0.0f, 1.0f);
                glVertex3f(1.0f, 1.0f, -1.0f);
                glVertex3f(1.0f, 1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, 1.0f);
                glVertex3f(1.0f, -1.0f, -1.0f);

                glEnd();
            } catch (Exception e) {
            }
        }

        private void processInput() {
            float mouseDX = Mouse.getDX() * 0.095f;
            float mouseDY = Mouse.getDY() * 0.095f;
            yaw(mouseDX);
            pitch(-mouseDY);

            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                System.out.println("mov forward");
                walkForward(.35f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                System.out.println("mov backward");
                walkBackwards(.35f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                System.out.println("mov left");
                strafeLeft(.35f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                System.out.println("mov right");
                strafeRight(.35f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                System.out.println("mov up");
                moveUp(.35f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                System.out.println("mov down");
                moveDown(.35f);
            }
        }

        public void lookThrough() {
            glLoadIdentity();
            glRotatef(pitch, 1.0f, 0.0f, 0.0f);
            glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            glTranslatef(-position.x, -position.y, -position.z);
        }

        private void updateView() {
            lookThrough();
        }

    }

}
