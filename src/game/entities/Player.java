package game.entities;

import com.jogamp.opengl.GL2;

/**
 * The player-controlled spaceship.
 * Handles movement with screen boundary constraints.
 */
public class Player extends GraphicalObject {
    private static final float SPEED = 5.0f;
    private static final float WIDTH = 40.0f;
    private static final float HEIGHT = 30.0f;
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

    @Override
    public void display(GL2 gl) {
        if (!active) return;

        gl.glColor3f(0.0f, 1.0f, 0.0f);  // Green player ship
        gl.glBegin(GL2.GL_TRIANGLES);

        // Main body (triangle pointing up)
        gl.glVertex2f(x, y + HEIGHT / 2);           // Top
        gl.glVertex2f(x - WIDTH / 2, y - HEIGHT / 2);  // Bottom left
        gl.glVertex2f(x + WIDTH / 2, y - HEIGHT / 2);  // Bottom right

        gl.glEnd();

        // Add side wings
        gl.glColor3f(0.0f, 0.8f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);

        // Left wing
        gl.glVertex2f(x - WIDTH / 2, y - HEIGHT / 2);
        gl.glVertex2f(x - WIDTH / 2 - 10, y - HEIGHT / 2 - 5);
        gl.glVertex2f(x - WIDTH / 2 - 5, y - HEIGHT / 2 - 5);
        gl.glVertex2f(x - WIDTH / 2, y - HEIGHT / 2);

        // Right wing
        gl.glVertex2f(x + WIDTH / 2, y - HEIGHT / 2);
        gl.glVertex2f(x + WIDTH / 2 + 10, y - HEIGHT / 2 - 5);
        gl.glVertex2f(x + WIDTH / 2 + 5, y - HEIGHT / 2 - 5);
        gl.glVertex2f(x + WIDTH / 2, y - HEIGHT / 2);

        gl.glEnd();
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
