package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Enemy alien that marches in formation.
 * Movement is controlled by the swarm logic in GameEngine.
 */
public class Alien extends GraphicalObject {
    private static final float WIDTH = 35.0f;
    private static final float HEIGHT = 25.0f;

    private final int type;  // Different alien types (0, 1, 2) for visual variety

    public Alien(float x, float y, int type) {
        super(x, y);
        this.type = type % 3;
    }

    /**
     * Move the alien by delta amounts (called by swarm controller)
     */
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    @Override
    public void update() {
        // Movement is handled externally by swarm logic
    }

    @Override
    public void display(GL2 gl) {
        if (!active) return;

        // Different colors for different types
        switch (type) {
            case 0:
                gl.glColor3f(0.0f, 0.0f, 1.0f);
                break;
            case 1:
                gl.glColor3f(1.0f, 0.5f, 0.0f);
                break;
            case 2:
                gl.glColor3f(0.8f, 0.8f, 0.0f);
                break;
        }

        // Body
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(x - WIDTH / 2, y + HEIGHT / 2);
        gl.glVertex2f(x + WIDTH / 2, y + HEIGHT / 2);
        gl.glVertex2f(x + WIDTH / 2, y - HEIGHT / 2);
        gl.glVertex2f(x - WIDTH / 2, y - HEIGHT / 2);
        gl.glEnd();

        // Eyes
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPointSize(5.0f);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex2f(x - 8, y + 5);
        gl.glVertex2f(x + 8, y + 5);
        gl.glEnd();

        // Antennae
        gl.glColor3f(0.8f, 0.8f, 0.8f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(x - 10, y + HEIGHT / 2);
        gl.glVertex2f(x - 15, y + HEIGHT / 2 + 10);
        gl.glVertex2f(x + 10, y + HEIGHT / 2);
        gl.glVertex2f(x + 15, y + HEIGHT / 2 + 10);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        return new float[] {
                x - WIDTH / 2,
                x + WIDTH / 2,
                y - HEIGHT / 2,
                y + HEIGHT / 2
        };
    }
}
