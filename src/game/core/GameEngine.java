package game.core;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import game.entities.*;

import java.util.*;

/**
 * The main game engine implementing GLEventListener.
 * Contains master lists of all game objects and handles the game loop.
 */
public class GameEngine implements GLEventListener {
    private static final float SCREEN_WIDTH = 800.0f;
    private static final float SCREEN_HEIGHT = 600.0f;

    private Player player;
    private List<Alien> aliens;
    private List<Bullet> bullets;
    private List<Particle> particles;
    private List<AlienBullet> alienBullets;

    // Swarm behavior
    private float swarmXSpeed = 2.0f;
    private int swarmDirection = 1;  // 1 = right, -1 = left
    private float swarmMoveTimer = 0;
    private static final float SWARM_MOVE_INTERVAL = 0.5f;  // Move every 0.5 seconds

    private final InputHandler inputHandler;

    private enum GameState { PLAYING, GAME_OVER, VICTORY }
    private GameState gameState = GameState.PLAYING;
    private int score = 0;

    // Utility
    private final Random random;
    private final GLU glu;

    public GameEngine(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        this.random = new Random();
        this.glu = new GLU();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Enable blending for transparency (particles)
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Smooth points
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);

        // Line smoothing
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);

        // Set background color (black for space)
        gl.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);

        // Initialize game objects
        initGame();
    }

    private void initGame() {
        // Initialize player
        player = new Player(SCREEN_WIDTH / 2, 50);

        // Initialize aliens in a grid
        aliens = new ArrayList<>();
        int rows = 5;
        int cols = 11;
        float startX = 100;
        float startY = 450;
        float spacingX = 60;
        float spacingY = 50;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * spacingX;
                float y = startY - row * spacingY;
                aliens.add(new Alien(x, y, row));
            }
        }

        // Initialize bullet and particle lists
        bullets = new ArrayList<>();
        particles = new ArrayList<>();
        alienBullets = new ArrayList<>();

        // Reset game state
        gameState = GameState.PLAYING;
        score = 0;
        swarmXSpeed = 2.0f;
        swarmDirection = 1;
        swarmMoveTimer = 0;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Clear the screen
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (gameState == GameState.PLAYING) {
            updateGame();
        }

        // Render all objects
        renderGame(gl);

        // Check for restart
        if ((gameState == GameState.GAME_OVER || gameState == GameState.VICTORY)
                && inputHandler.isRestartPressed()) {
            initGame();
        }
    }

    private void updateGame() {
        // Update player movement
        float vx = 0;
        if (inputHandler.isLeftPressed()) vx -= 1;
        if (inputHandler.isRightPressed()) vx += 1;
        player.setVelocityX(vx);
        player.update();

        // Handle shooting
        if (inputHandler.isFirePressed()) {
            bullets.add(new Bullet(player.getX(), player.getY() + 20));
        }

        // Update bullets
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update();
        }

        // Update alien bullets
        alienBullets.removeIf(alienBullet -> !alienBullet.isActive());
        for (AlienBullet ab: alienBullets) ab.update();

        // Update swarm movement
        updateSwarmMovement();

        // Update aliens
        aliens.removeIf(alien -> !alien.isActive());
        for (Alien alien : aliens) {
            alien.update();
        }

        // Update particles
        particles.removeIf(particle -> !particle.isActive());
        for (Particle particle : particles) {
            particle.update();
        }

        // Collision detection
        checkCollisions();

        // Check win/lose conditions
        if (aliens.isEmpty()) {
            gameState = GameState.VICTORY;
        }

        // Check if any alien reached the player
        for (Alien alien : aliens) {
            if (alien.getY() < 100) {
                gameState = GameState.GAME_OVER;
                break;
            }
        }
    }

    private void updateSwarmMovement() {
        swarmMoveTimer += 0.016f;  // Approximately 1/60th of a second

        if (swarmMoveTimer >= SWARM_MOVE_INTERVAL) {
            swarmMoveTimer = 0;

            // Move all aliens
            float moveAmount = Math.abs(swarmXSpeed) * swarmDirection;
            boolean hitEdge = false;

            Map<Float, Alien> frontLineAliens = new HashMap<>();

            for (Alien alien : aliens) {
                if(!alien.isActive()) continue;

                alien.move(moveAmount, 0);

                float xKey = alien.getX();
                if(!frontLineAliens.containsKey(xKey) || alien.getY() < frontLineAliens.get(xKey).getY()) {
                    frontLineAliens.put(xKey, alien);
                }

                // Check if any alien hit the edge
                if (alien.getX() < 50 || alien.getX() > SCREEN_WIDTH - 50) {
                    hitEdge = true;
                }
            }

            // If hit edge, move down and reverse direction
            if (hitEdge) {
                for (Alien alien : aliens) {
                    if (!alien.isActive()) continue;
                    alien.move(-moveAmount, -20);  // Undo horizontal, move down
                }
                swarmDirection *= -1;
                swarmXSpeed *= 1.1f;  // Speed up
            }

            // Shooting Logic, only the front-line aliens fire
            // The chance increases as the swarmXSpeed increases
            float shootChance = 0.05f * (Math.abs(swarmXSpeed) / 2.0f);
            shootChance = Math.min(shootChance, 0.15f);

            for (Alien shooter : frontLineAliens.values()) {
                if (random.nextFloat() < shootChance) {
                    alienBullets.add(new AlienBullet(shooter.getX(), shooter.getY() - 15));
                }
            }
        }
    }

    private void checkCollisions() {
        // Bullet vs Alien collisions
        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) continue;

            for (Alien alien : aliens) {
                if (!alien.isActive()) continue;

                if (bullet.collidesWith(alien)) {
                    bullet.setActive(false);
                    alien.setActive(false);
                    score += 10;
                    spawnExplosion(alien.getX(), alien.getY());
                    break;
                }
            }
        }

        // Player Bullet vs Alien Bullet
        for (Bullet pb : bullets) {
            if(!pb.isActive()) continue;

            for (AlienBullet ab : alienBullets) {
                if(!ab.isActive()) continue;

                if(pb.collidesWith(ab)) {
                    pb.setActive(false);
                    ab.setActive(false);
                    spawnExplosion(ab.getX(), ab.getY());
                    break;
                }
            }
        }

        // Alien Bullet vs Player
        for (AlienBullet ab : alienBullets) {
            if(!ab.isActive()) continue;
            if(ab.collidesWith(player)) {
                ab.setActive(false);
                gameState = GameState.GAME_OVER;
                spawnExplosion(player.getX(), player.getY());
                break;
            }
        }

        // Alien vs Player collisions
        for (Alien alien : aliens) {
            if (!alien.isActive()) continue;

            if (alien.collidesWith(player)) {
                gameState = GameState.GAME_OVER;
                spawnExplosion(player.getX(), player.getY());
                break;
            }
        }
    }

    private void spawnExplosion(float x, float y) {
        int particleCount = 20;

        for (int i = 0; i < particleCount; i++) {
            float vx = (random.nextFloat() - 0.5f) * 5;
            float vy = (random.nextFloat() - 0.5f) * 5;

            // Random colors (yellow to red)
            float r = 1.0f;
            float g = random.nextFloat();
            float b = 0.0f;

            particles.add(new Particle(x, y, vx, vy, r, g, b));
        }
    }

    private void renderGame(GL2 gl) {
        // Render player
        if (gameState == GameState.PLAYING) {
            player.display(gl);
        }

        // Render aliens
        for (Alien alien : aliens) {
            alien.display(gl);
        }

        // Render bullets
        for (Bullet bullet : bullets) {
            bullet.display(gl);
        }

        // Render alien bullets
        for (AlienBullet ab : alienBullets) {
            ab.display(gl);
        }

        // Render particles
        for (Particle particle : particles) {
            particle.display(gl);
        }

        // Render UI
        renderUI(gl);
    }

    private void renderUI(GL2 gl) {
        // Simple score display (you can enhance this with text rendering)
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        // Draw score indicator (simple bars representing score/10)
        int scoreBars = score / 10;
        for (int i = 0; i < Math.min(scoreBars, 55); i++) {
            float x = 10 + i * 12;
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(x, SCREEN_HEIGHT - 20);
            gl.glVertex2f(x + 8, SCREEN_HEIGHT - 20);
            gl.glVertex2f(x + 8, SCREEN_HEIGHT - 10);
            gl.glVertex2f(x, SCREEN_HEIGHT - 10);
            gl.glEnd();
        }

        // Game state messages
        if (gameState == GameState.GAME_OVER) {
            // Draw "GAME OVER" indicator
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 - 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 + 100, SCREEN_HEIGHT / 2 - 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 + 100, SCREEN_HEIGHT / 2 + 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 30);
            gl.glEnd();
        } else if (gameState == GameState.VICTORY) {
            // Draw "VICTORY" indicator
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 - 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 + 100, SCREEN_HEIGHT / 2 - 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 + 100, SCREEN_HEIGHT / 2 + 30);
            gl.glVertex2f(SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 30);
            gl.glEnd();
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        // Prevent division by zero
        if (height == 0) height = 1;

        gl.glViewport(0, 0, width, height);

        // Set up orthographic projection (2D)
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Fixed coordinate system: 800x600
        glu.gluOrtho2D(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Cleanup if needed
    }
}
