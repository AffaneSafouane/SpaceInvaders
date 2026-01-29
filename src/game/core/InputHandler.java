package game.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input using boolean flags for smooth movement.
 * This prevents the delay and repeat issues of using keyPressed alone.
 */
public class InputHandler implements KeyListener {
    // Movement flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private boolean spaceWasPressed = false;  // For single-shot firing

    // Game control flags
    private boolean restartPressed = false;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;
            case KeyEvent.VK_R:
                restartPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                spaceWasPressed = false;
                break;
            case KeyEvent.VK_R:
                restartPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    // Getters for game logic
    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    /**
     * Returns true only once per space press (for single-shot firing)
     */
    public boolean isFirePressed() {
        if (spacePressed && !spaceWasPressed) {
            spaceWasPressed = true;
            return true;
        }
        return false;
    }

    public boolean isRestartPressed() {
        return restartPressed;
    }
}
