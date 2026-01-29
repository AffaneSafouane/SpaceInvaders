package game.entities;

import com.jogamp.opengl.GL2;

public class WallBricks extends GraphicalObject {
    public static final float SIZE = 10.0f;

    public WallBricks(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {}

    @Override
    public void display(GL2 gl) {
        if (!active) return;
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(x - SIZE/2, y + SIZE/2);
        gl.glVertex2f(x + SIZE/2, y + SIZE/2);
        gl.glVertex2f(x + SIZE/2, y - SIZE/2);
        gl.glVertex2f(x - SIZE/2, y - SIZE/2);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        return new float[] { x - SIZE/2, x + SIZE/2, y - SIZE/2, y + SIZE/2 };
    }
}
