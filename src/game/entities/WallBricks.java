package game.entities;

import com.jogamp.opengl.GL2;

public class WallBricks extends GraphicalObject {
    public static final float SIZE = 10.0f;
    private static final float DEPTH_RATIO = 0.5f;

    public WallBricks(float x, float y, float z) {
        super(x, y, z, 0, 0, 0, 0.0f, 1.0f, 0.0f, 10.0f);
    }

    @Override
    public void displayNormalized(GL2 gl) {
        if (!active) return;

        float s = 0.5f;
        float d = DEPTH_RATIO;

        gl.glBegin(GL2.GL_QUADS);

        // Front Face
        gl.glVertex3f(-s, -s,  d);
        gl.glVertex3f( s, -s,  d);
        gl.glVertex3f( s,  s,  d);
        gl.glVertex3f(-s,  s,  d);

        // Right Side
        gl.glColor4f(0.0f, 0.7f, 0.0f, this.a);
        gl.glVertex3f( s, -s, -d);
        gl.glVertex3f( s,  s, -d);
        gl.glVertex3f( s,  s,  d);
        gl.glVertex3f( s, -s,  d);

        // Top Side
        gl.glColor4f(0.0f, 0.85f, 0.0f, this.a);
        gl.glVertex3f(-s,  s, -d);
        gl.glVertex3f(-s,  s,  d);
        gl.glVertex3f( s,  s,  d);
        gl.glVertex3f( s,  s, -d);

        // Left Side
        gl.glColor4f(0.0f, 0.7f, 0.0f, this.a);
        gl.glVertex3f(-s, -s, -d);
        gl.glVertex3f(-s, -s,  d);
        gl.glVertex3f(-s,  s,  d);
        gl.glVertex3f(-s,  s, -d);

        gl.glEnd();
    }

    @Override
    public void update() {}

    @Override
    public float[] getBounds() {
        float s = 10.0f / 2f;
        float d = (10.0f * DEPTH_RATIO) / 2f;

        return new float[] {
                getX() - s, getX() + s,
                getY() - s, getY() + s,
                getZ() - d, getZ() + d
        };
    }
}