package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Projectile fired by the aliens.
 * Moves downward and deactivates when leaving the screen.
 */
public class AlienBullet extends GraphicalObject {
    private static final float SPEED = 8.0f;
    private static final float WIDTH = 3.0f;
    private static final float HEIGHT = 15.0f;

    public AlienBullet(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        if (!active) return;

        // Move downward
        y -= SPEED;

        // Deactivate if off-screen
        if (y + (HEIGHT / 2) < 0) {
            active = false;
        }
    }

    @Override
    public void display(GL2 gl) {
        if (!active) return;

        gl.glColor3f(1.0f, 1.0f, 1.0f);  // White bullet
        gl.glBegin(GL2.GL_QUADS);

        gl.glVertex2f(x - WIDTH, y + HEIGHT / 2);
        gl.glVertex2f(x + WIDTH, y + HEIGHT / 2);
        gl.glVertex2f(x + WIDTH, y - HEIGHT / 2);
        gl.glVertex2f(x - WIDTH, y - HEIGHT / 2);

        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        return new float[] {
                x - WIDTH,
                x + WIDTH,
                y - HEIGHT / 2,
                y + HEIGHT / 2
        };
    }
}
