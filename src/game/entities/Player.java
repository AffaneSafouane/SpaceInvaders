package game.entities;

import com.jogamp.opengl.GL2;

/**
 * The player-controlled spaceship.
 * Now supports a Z coordinate and 3D AABB bounds for 2.5D rendering.
 */
public class Player extends GraphicalObject {
    private static final float SPEED = 5.0f;
    public static final float WIDTH = 40.0f;
    public static final float HEIGHT = 30.0f;
    private static final float SCREEN_WIDTH = 800.0f;
    private static final float DEPTH = 0.1f; // small thickness in Z

    private float velocityX = 0;

    public Player(float x, float y, float z) {
        super(x, y, z, 0, 0, 0, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    /**
     * Set horizontal velocity based on input
     */
    public void setVelocityX(float vx) {
        this.velocityX = vx;
    }

    @Override
    public void update() {
        if (!active) return;

        float newX = getX() + velocityX * SPEED;

        // Boundary checks
        if (newX < WIDTH / 2) newX = WIDTH / 2;
        if (newX > SCREEN_WIDTH - WIDTH / 2) newX = SCREEN_WIDTH - WIDTH / 2;

        setX(newX);
    }

    @Override
    public void displayNormalized(GL2 gl) {
        if (!active) return;

        float x = 0;
        float y = 0;
        float z = 0;
        float w = WIDTH;
        float h = HEIGHT;

        float hw = w / 2;
        float hh = h / 2;
        float depth = 10.0f;

        gl.glBegin(GL2.GL_TRIANGLES);

        // Main body
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(x, y + hh, z + depth/2);
        gl.glColor3f(0.0f, 0.8f, 0.0f);
        gl.glVertex3f(x - hw, y - hh, z + depth/2);
        gl.glColor3f(0.0f, 0.9f, 0.0f);
        gl.glVertex3f(x + hw, y - hh, z + depth/2);

        // BACK face
        gl.glColor3f(0.0f, 0.5f, 0.0f);
        gl.glVertex3f(x, y + hh, z - depth/2);
        gl.glColor3f(0.0f, 0.4f, 0.0f);
        gl.glVertex3f(x + hw, y - hh, z - depth/2);
        gl.glColor3f(0.0f, 0.4f, 0.0f);
        gl.glVertex3f(x - hw, y - hh, z - depth/2);

        // Left side
        gl.glColor3f(0.0f, 0.7f, 0.0f);
        gl.glVertex3f(x - hw, y - hh, z + depth/2);
        gl.glVertex3f(x - hw, y - hh, z - depth/2);
        gl.glVertex3f(x, y + hh, z - depth/2);

        gl.glVertex3f(x, y + hh, z - depth/2);
        gl.glVertex3f(x, y + hh, z + depth/2);
        gl.glVertex3f(x - hw, y - hh, z + depth/2);

        // Right side
        gl.glColor3f(0.0f, 0.7f, 0.0f);
        gl.glVertex3f(x + hw, y - hh, z + depth/2);
        gl.glVertex3f(x + hw, y - hh, z - depth/2);
        gl.glVertex3f(x, y + hh, z - depth/2);

        gl.glVertex3f(x, y + hh, z - depth/2);
        gl.glVertex3f(x, y + hh, z + depth/2);
        gl.glVertex3f(x + hw, y - hh, z + depth/2);

        gl.glEnd();

        // Cockpit
        gl.glBegin(GL2.GL_QUADS);
        // Front face of cockpit
        gl.glColor3f(0.2f, 0.6f, 0.2f);
        gl.glVertex3f(x - hw/3, y, z + depth/2 + 1);
        gl.glVertex3f(x + hw/3, y, z + depth/2 + 1);
        gl.glColor3f(0.0f, 0.4f, 0.0f);
        gl.glVertex3f(x + hw/3, y - hh/2, z + depth/2 + 1);
        gl.glVertex3f(x - hw/3, y - hh/2, z + depth/2 + 1);

        // Back face of cockpit
        gl.glColor3f(0.1f, 0.3f, 0.1f);
        gl.glVertex3f(x - hw/3, y, z - depth/2 - 1);
        gl.glVertex3f(x + hw/3, y, z - depth/2 - 1);
        gl.glVertex3f(x + hw/3, y - hh/2, z - depth/2 - 1);
        gl.glVertex3f(x - hw/3, y - hh/2, z - depth/2 - 1);

        // Top of cockpit
        gl.glColor3f(0.3f, 0.8f, 0.3f);
        gl.glVertex3f(x - hw/3, y, z + depth/2 + 1);
        gl.glVertex3f(x + hw/3, y, z + depth/2 + 1);
        gl.glVertex3f(x + hw/3, y, z - depth/2 - 1);
        gl.glVertex3f(x - hw/3, y, z - depth/2 - 1);
        gl.glEnd();

        // Wings
        gl.glBegin(GL2.GL_QUADS);
        // Left wing
        gl.glColor3f(0.0f, 0.7f, 0.0f);
        gl.glVertex3f(x - hw, y - hh, z + depth/2);
        gl.glVertex3f(x - hw - w/4, y - hh - h/6, z + depth/2);
        gl.glVertex3f(x - hw - w/4, y - hh - h/6, z - depth/2);
        gl.glVertex3f(x - hw, y - hh, z - depth/2);

        // Right wing
        gl.glColor3f(0.0f, 0.7f, 0.0f);
        gl.glVertex3f(x + hw, y - hh, z + depth/2);
        gl.glVertex3f(x + hw + w/4, y - hh - h/6, z + depth/2);
        gl.glVertex3f(x + hw + w/4, y - hh - h/6, z - depth/2);
        gl.glVertex3f(x + hw, y - hh, z - depth/2);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        float halfW = WIDTH / 2f;
        float halfH = HEIGHT / 2f;
        float halfD = DEPTH / 2f;

        return new float[] {
                getX() - halfW, getX() + halfW,
                getY() - halfH, getY() + halfH,
                getZ() - halfD, getZ() + halfD
        };
    }
}
