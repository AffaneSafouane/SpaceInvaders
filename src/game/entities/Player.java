package game.entities;

import com.jogamp.opengl.GL2;

/**
 * The player-controlled spaceship.
 * Handles movement with screen boundary constraints.
 */
public class Player extends GraphicalObject {
    private static final float SPEED = 5.0f;
    public static final float WIDTH = 40.0f;
    public static final float HEIGHT = 30.0f;
    private static final float SCREEN_WIDTH = 800.0f;

    private float velocityX = 0;

    public Player(float x, float y) {
        super(x, y);
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

        // Update position
        x += velocityX * SPEED;

        // Enforce screen boundaries
        if (x < WIDTH / 2) {
            x = WIDTH / 2;
        } else if (x > SCREEN_WIDTH - WIDTH / 2) {
            x = SCREEN_WIDTH - WIDTH / 2;
        }
    }

    public static void displayNormalized(GL2 gl, float x, float y, float width, float height) {
        gl.glColor3f(0.0f, 1.0f, 0.0f);  // Green player ship
        gl.glBegin(GL2.GL_TRIANGLES);

        // Main body (triangle pointing up)
        gl.glVertex2f(x, y + height / 2);
        gl.glVertex2f(x - width / 2, y - height / 2);
        gl.glVertex2f(x + width / 2, y - height / 2);
        gl.glEnd();

        // Add side wings (proportional to width/height)
        float wingW = width / 4;
        float wingH = height / 6;

        gl.glColor3f(0.0f, 0.8f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        // Left wing
        gl.glVertex2f(x - width / 2, y - height / 2);
        gl.glVertex2f(x - width / 2 - wingW, y - height / 2 - wingH);
        gl.glVertex2f(x - width / 2 - wingW/2, y - height / 2 - wingH);
        gl.glVertex2f(x - width / 2, y - height / 2);

        // Right wing
        gl.glVertex2f(x + width / 2, y - height / 2);
        gl.glVertex2f(x + width / 2 + wingW, y - height / 2 - wingH);
        gl.glVertex2f(x + width / 2 + wingW/2, y - height / 2 - wingH);
        gl.glVertex2f(x + width / 2, y - height / 2);
        gl.glEnd();
    }

    @Override
    public void display(GL2 gl) {
        if (!active) return;

        displayNormalized(gl, x, y, WIDTH, HEIGHT);
    }

    @Override
    public float[] getBounds() {
        return new float[] {
                x - WIDTH / 2,      // minX
                x + WIDTH / 2,      // maxX
                y - HEIGHT / 2,     // minY
                y + HEIGHT / 2      // maxY
        };
    }
}
