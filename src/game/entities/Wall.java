package game.entities;

import com.jogamp.opengl.GL2;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    private final List<WallBricks> bricks;

    public Wall(float centerX, float centerY) {
        bricks = new ArrayList<>();
        int rows = 6;
        int cols = 8;
        float spacing = WallBricks.SIZE;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Skip top corners for the "rounded" effect
                if (r == rows - 1 && (c == 0 || c == cols - 1)) continue;
                // Skip bottom middle for the "arch" tunnel
                if (r < 2 && (c >= 2 && c <= 5)) continue;

                float x = centerX + (c - cols / 2.0f) * spacing;
                float y = centerY + (r - rows / 2.0f) * spacing;
                bricks.add(new WallBricks(x, y));
            }
        }
    }

    public List<WallBricks> getBricks() {
        return bricks;
    }

    public void display(GL2 gl) {
        for (WallBricks wb : bricks) {
            wb.display(gl);
        }
    }

    public boolean isDestroyed() {
        for (WallBricks brick : bricks) {
            if (brick.isActive()) return false;
        }
        return true;
    }
}
