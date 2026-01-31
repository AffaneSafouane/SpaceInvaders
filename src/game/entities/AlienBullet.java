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
        super(x, y, 0.0f, 0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void update() {
        if (!active) return;

        // Move downward
        translate(0, -SPEED, 0);

        // Deactivate if off-screen
        if (getY() + (HEIGHT / 2) < 0) {
            active = false;
        }
    }

    @Override
    public void displayNormalized(GL2 gl) {
        float hw = WIDTH / 2f;
        float hh = HEIGHT / 2f;

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hw,  hh, 0);
        gl.glVertex3f( hw,  hh, 0);
        gl.glVertex3f( hw, -hh, 0);
        gl.glVertex3f(-hw, -hh, 0);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        float hw = WIDTH / 2f;
        float hh = HEIGHT / 2f;
        float halfDepth = 0.5f;

        return new float[] {
                getX() - hw, getX() + hw,
                getY() - hh, getY() + hh,
                getZ() - halfDepth, getZ() + halfDepth
        };
    }
}
