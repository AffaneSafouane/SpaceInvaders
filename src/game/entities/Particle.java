package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Particle for explosion effects.
 * Has velocity (vx, vy) and a life value that decreases over time.
 */
public class Particle extends GraphicalObject {
    private final float vx;
    private float vy;           // Velocity components
    private float life;         // Life remaining (1.0 to 0.0)
    private static final float DECAY_RATE = 0.02f;
    private static final float SIZE = 3.0f;

    private final float r, g, b;  // Color

    public Particle(float x, float y, float vx, float vy, float r, float g, float b) {
        super(x, y);
        this.vx = vx;
        this.vy = vy;
        this.life = 1.0f;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void update() {
        if (!active) return;

        // Update position based on velocity
        x += vx;
        y += vy;

        // Apply gravity/deceleration
        vy -= 0.1f;

        // Decay life
        life -= DECAY_RATE;

        // Deactivate when life runs out
        if (life <= 0) {
            active = false;
        }
    }

    @Override
    public void display(GL2 gl) {
        if (!active) return;

        // Set color with alpha based on life
        gl.glColor4f(r, g, b, life);

        // Draw as a point
        gl.glPointSize(SIZE);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(x, y);
        gl.glEnd();

        // Optional: Draw as a small quad for more visibility
        float halfSize = SIZE / 2;
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(x - halfSize, y + halfSize);
        gl.glVertex2f(x + halfSize, y + halfSize);
        gl.glVertex2f(x + halfSize, y - halfSize);
        gl.glVertex2f(x - halfSize, y - halfSize);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        // Particles don't need collision detection
        return new float[] {x, x, y, y};
    }
}
