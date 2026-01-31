package game.entities;

import com.jogamp.opengl.GL2;

public class Alien extends GraphicalObject {
    private static final float WIDTH = 35.0f;
    private static final float HEIGHT = 25.0f;
    private static final float DEPTH = 15.0f;

    private final int type;

    public Alien(float x, float y, float z, int type) {
        super(x, y, z, 0, 0, 0, 1.0f, 1.0f, 1.0f, 1.0f);
        this.type = type % 3;
        assignTypeColor();
    }

    private void assignTypeColor() {
        switch (type) {
            case 0: setColor(0.0f, 0.5f, 1.0f); break; // Blue
            case 1: setColor(1.0f, 0.5f, 0.0f); break; // Orange
            case 2: setColor(0.8f, 0.8f, 0.0f); break; // Yellow
        }
    }

    public void move(float dx, float dy) {
        translate(dx, dy, 0);
    }

    @Override
    public void update() {
        // Logic handled by Swarm
    }

    @Override
    public void displayNormalized(GL2 gl) {
        float hw = WIDTH / 2f;
        float hh = HEIGHT / 2f;
        float hd = DEPTH / 2f;

        // Body
        drawAlienBody(gl, hw, hh, hd);

        // Eyes
        gl.glColor4f(1.0f, 1.0f, 1.0f, this.a);

        float eyeSize = hh * 0.25f;

        float eyeXOffset = hw * 0.4f;
        float eyeYPos = hh * 0.3f;

        drawSimpleCube(gl, -eyeXOffset, eyeYPos, hd + 0.5f, eyeSize);
        drawSimpleCube(gl, eyeXOffset, eyeYPos, hd + 0.5f, eyeSize);

        // Antennae
        gl.glColor4f(0.8f, 0.8f, 0.8f, this.a);
        gl.glLineWidth(2.0f);
        gl.glBegin(GL2.GL_LINES);

        // Left antenna
        float antennaStartX = -hw * 0.7f;
        gl.glVertex3f(antennaStartX, hh, hd + 1.0f);
        gl.glVertex3f(antennaStartX - hw * 0.2f, hh + hh * 0.4f, hd + 1.0f);

        // Right antenna
        antennaStartX = hw * 0.7f;
        gl.glVertex3f(antennaStartX, hh, hd + 1.0f);
        gl.glVertex3f(antennaStartX + hw * 0.2f, hh + hh * 0.4f, hd + 1.0f);

        gl.glEnd();
        gl.glLineWidth(1.0f);
    }

    private void drawAlienBody(GL2 gl, float hw, float hh, float hd) {
        float[] baseColor = {0,0,0,0};
        gl.glGetFloatv(GL2.GL_CURRENT_COLOR, baseColor, 0);

        gl.glBegin(GL2.GL_QUADS);

        // Front
        gl.glColor4f(baseColor[0], baseColor[1], baseColor[2], this.a);
        gl.glVertex3f(-hw, -hh, hd); gl.glVertex3f(hw, -hh, hd);
        gl.glVertex3f(hw, hh, hd); gl.glVertex3f(-hw, hh, hd);

        // Back
        gl.glColor4f(baseColor[0]*0.5f, baseColor[1]*0.5f, baseColor[2]*0.5f, this.a);
        gl.glVertex3f(-hw, -hh, -hd); gl.glVertex3f(-hw, hh, -hd);
        gl.glVertex3f(hw, hh, -hd); gl.glVertex3f(hw, -hh, -hd);

        // Top
        gl.glColor4f(baseColor[0]*0.8f, baseColor[1]*0.8f, baseColor[2]*0.8f, this.a);
        gl.glVertex3f(-hw, hh, -hd); gl.glVertex3f(-hw, hh, hd);
        gl.glVertex3f(hw, hh, hd); gl.glVertex3f(hw, hh, -hd);

        // Bottom
        gl.glColor4f(baseColor[0]*0.4f, baseColor[1]*0.4f, baseColor[2]*0.4f, this.a);
        gl.glVertex3f(-hw, -hh, -hd); gl.glVertex3f(hw, -hh, -hd);
        gl.glVertex3f(hw, -hh, hd); gl.glVertex3f(-hw, -hh, hd);

        // Right side
        gl.glColor4f(baseColor[0]*0.7f, baseColor[1]*0.7f, baseColor[2]*0.7f, this.a);
        gl.glVertex3f(hw, -hh, -hd); gl.glVertex3f(hw, hh, -hd);
        gl.glVertex3f(hw, hh, hd); gl.glVertex3f(hw, -hh, hd);

        // Left side
        gl.glColor4f(baseColor[0]*0.7f, baseColor[1]*0.7f, baseColor[2]*0.7f, this.a);
        gl.glVertex3f(-hw, -hh, -hd); gl.glVertex3f(-hw, -hh, hd);
        gl.glVertex3f(-hw, hh, hd); gl.glVertex3f(-hw, hh, -hd);

        gl.glEnd();
    }

    private void drawSimpleCube(GL2 gl, float cx, float cy, float cz, float size) {
        float s = size / 2f;
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(cx-s, cy-s, cz); gl.glVertex3f(cx+s, cy-s, cz);
        gl.glVertex3f(cx+s, cy+s, cz); gl.glVertex3f(cx-s, cy+s, cz);
        gl.glEnd();
    }

    @Override
    public float[] getBounds() {
        float hw = WIDTH / 2f;
        float hh = HEIGHT / 2f;
        float hd = DEPTH / 2f;
        return new float[]{
                getX() - hw, getX() + hw,
                getY() - hh, getY() + hh,
                getZ() - hd, getZ() + hd
        };
    }
}