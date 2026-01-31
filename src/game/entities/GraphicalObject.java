package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Abstract base class for all renderable game objects.
 * Handles position, activity state, and provides abstract methods for update/render logic.
 */
public abstract class GraphicalObject {
    private float posX, posY, posZ;
    private float angX, angY, angZ;
    private float r, g, b;
    private float scale;
    protected float a = 1.0f;
    protected boolean active;

    public void setAlpha(float a) { this.a = Math.max(0f, Math.min(1f, a)); }

    public void setColor(float r, float g, float b) {
        this.r = r; this.g = g; this.b = b;
    }

    public void setScale(float scale) { this.scale = scale; }

    public GraphicalObject(float pX, float pY, float pZ,
                           float angX, float angY, float angZ,
                           float r, float g, float b,
                           float scale)
    {
        this.posX = pX;
        this.posY = pY;
        this.posZ = pZ;
        this.angX = angX;
        this.angY = angY;
        this.angZ = angZ;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0f;
        this.scale = scale;
        this.active = true;
    }

    public abstract void displayNormalized(GL2 gl);

    public void display(GL2 gl)
    {


        gl.glPushMatrix();
        gl.glTranslatef(this.posX, this.posY, this.posZ);
        gl.glRotatef(this.angX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(this.angY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(this.angZ, 0.0f, 0.0f, 1.0f);
        gl.glScalef(this.scale, this.scale, this.scale);
        gl.glColor4f(this.r, this.g, this.b, this.a);
        this.displayNormalized(gl);
        gl.glPopMatrix();
    }

    public void rotate(float aX,float aY,float aZ)
    {
        this.angX += aX;
        this.angY += aY;
        this.angZ += aZ;
    }

    public void translate(float pX,float pY,float pZ)
    {
        this.posX += pX;
        this.posY += pY;
        this.posZ += pZ;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public float getZ() {
        return posZ;
    }

    public void setX(float posX) {
        this.posX = posX;
    }

    public void setY(float posY) {
        this.posY = posY;
    }

    public void setZ(float posZ) {
        this.posZ = posZ;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public void setPosition(float x, float y) {
        setPosition(x, y, this.posZ);
    }

    public void setPosition(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    /**
     * Update game logic (movement, collision detection, etc.)
     * Called once per frame.
     */
    public abstract void update();

    /**
     * Get the bounding box for collision detection.
     * Returns [minX, maxX, minY, maxY, minZ, maxZ]
     */
    public abstract float[] getBounds();

    /**
     * 3D AABB collision detection helper
     */
    public boolean collidesWith(GraphicalObject other) {
        float[] a = this.getBounds();
        float[] b = other.getBounds();

        // indices: 0=minX, 1=maxX, 2=minY, 3=maxY, 4=minZ, 5=maxZ
        return a[0] < b[1] && // a.minX < b.maxX
                a[1] > b[0] && // a.maxX > b.minX
                a[2] < b[3] && // a.minY < b.maxY
                a[3] > b[2] && // a.maxY > b.minY
                a[4] < b[5] && // a.minZ < b.maxZ
                a[5] > b[4];   // a.maxZ > b.minZ
    }
}