package game.core;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the Space Invaders clone.
 * Sets up the JFrame, GLCanvas, and FPSAnimator.
 */
public class Main {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int FPS = 60;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCanvas canvas = getGlCanvas(profile);

        FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.requestFocusInWindow();

        animator.start();

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                animator.stop();
            }
        });
    }

    private static GLCanvas getGlCanvas(GLProfile profile) {
        GLCapabilities capabilities = new GLCapabilities(profile);

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        InputHandler inputHandler = new InputHandler();
        canvas.addKeyListener(inputHandler);

        GameEngine gameEngine = new GameEngine(inputHandler);
        canvas.addGLEventListener(gameEngine);
        return canvas;
    }
}
