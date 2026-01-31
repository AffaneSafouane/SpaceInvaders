package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Particle for explosion effects.
 * Has velocity (vx, vy) and a life value that decreases over time.
 */
public class Particle extends GraphicalObject {
    private final float vx;
    private float vy;
    private float life;
    private static final float DECAY_RATE = 0.02f;
    private static final float SIZE = 3.0f;

    public Particle(float x, float y, float vx, float vy, float r, float g, float b) {
        super(x, y, 0.0f, 0, 0, 0, r, g, b, 1.0f);
        this.vx = vx;
        this.vy = vy;
        this.life = 1.0f;
    }

    @Override
    public void update() {
        if (!active) return;

        translate(vx, vy, 0);

        vy -= 0.1f;

        life -= DECAY_RATE;
        setAlpha(life);

        if (life <= 0) {
            active = false;
        }
    }

    @Override
    public void displayNormalized(GL2 gl) {
        float hs = SIZE / 2f;

        // Draw as a small 2D quad in the 3D world
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-hs,  hs, 0);
        gl.glVertex3f( hs,  hs, 0);
        gl.glVertex3f( hs, -hs, 0);
        gl.glVertex3f(-hs, -hs, 0);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        return new float[] { getX(), getX(), getY(), getY(), getZ(), getZ() };
    }
}
