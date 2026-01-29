package game.entities;

import com.jogamp.opengl.GL2;

/**
 * Abstract base class for all renderable game objects.
 * Handles position, activity state, and provides abstract methods for update/render logic.
 */
public abstract class GraphicalObject {
    protected float x, y;           // Position in world coordinates
    protected boolean active;       // Whether this object is alive and should be processed

    public GraphicalObject(float x, float y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Update game logic (movement, collision detection, etc.)
     * Called once per frame.
     */
    public abstract void update();

    /**
     * Render this object using OpenGL.
     * @param gl The OpenGL context
     */
    public abstract void display(GL2 gl);

    /**
     * Get the bounding box for collision detection.
     * Returns [minX, maxX, minY, maxY]
     */
    public abstract float[] getBounds();

    /**
     * AABB collision detection helper
     */
    public boolean collidesWith(GraphicalObject other) {
        float[] thisBounds = this.getBounds();
        float[] otherBounds = other.getBounds();

        return thisBounds[0] < otherBounds[1] &&  // this.minX < other.maxX
                thisBounds[1] > otherBounds[0] &&  // this.maxX > other.minX
                thisBounds[2] < otherBounds[3] &&  // this.minY < other.maxY
                thisBounds[3] > otherBounds[2];    // this.maxY > other.minY
    }
}
