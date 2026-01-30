package game.core;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import game.entities.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import com.jogamp.opengl.util.awt.TextRenderer;

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
    private List<Wall> walls;

    // Swarm behavior
    private float swarmXSpeed = 2.0f;
    private int swarmDirection = 1;  // 1 = right, -1 = left
    private float swarmMoveTimer = 0;
    private static final float SWARM_MOVE_INTERVAL = 0.5f;  // Move every 0.5 seconds

    private final InputHandler inputHandler;

    private enum GameState { PLAYING, GAME_OVER, VICTORY, LEVEL_UP }
    private GameState gameState = GameState.PLAYING;
    private int score = 0;
    private int level = 1;
    private float levelTransitionTimer = 0;
    private static final float TRANSITION_DELAY = 2.0f;
    private int lives = 3;

    private boolean isRespawning = false;
    private float respawnTimer = 0;
    private static final float RESPAWN_DELAY = 1.5f;

    private static final int MAX_LEVEL = 3;

    // Utility
    private final Random random;
    private final GLU glu;
    private TextRenderer textRenderer;

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

        textRenderer = new TextRenderer((new Font("Monospaced", Font.BOLD, 36)));

        // Initialize game objects
        initGame(true);
    }

    private void initGame(boolean resetLevel) {
        // Initialize player
        player = new Player(SCREEN_WIDTH / 2, 70);

        if (resetLevel) {
            level = 1;
            score = 0;
            lives = 3;
        }

        // Initialize aliens in a grid
        aliens = new ArrayList<>();
        int rows = 5;
        int cols = 11;
        float startX = 100;

        // Difficulty Scaling
        float startY = Math.max(450 - (level - 1) * 30, 250);
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

        // Initialize walls
        walls = new ArrayList<>();
        int wallCount = 4;
        float wallY = 150;
        for (int i = 0; i < wallCount; i++) {
            float x = (SCREEN_WIDTH / (wallCount + 1)) * (i + 1);
            walls.add(new Wall(x, wallY));
        }

        // Reset game state
        gameState = GameState.PLAYING;
        swarmXSpeed = 2.0f + (level * 0.5f);
        swarmDirection = 1;
        swarmMoveTimer = 0;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Clear the screen
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (gameState == GameState.PLAYING || gameState == GameState.VICTORY || gameState == GameState.LEVEL_UP) {
            updateGame();
        }

        // Render all objects
        renderGame(gl);

        // Check for restart
        if ((gameState == GameState.GAME_OVER)
                && inputHandler.isRestartPressed()) {
            initGame(true);
        }
    }

    private void updateGame() {
        // Update particles
        particles.removeIf(particle -> !particle.isActive());
        for (Particle particle : particles) {
            particle.update();
        }

        if (isRespawning) {
            respawnTimer -= 0.016f;
            if (respawnTimer <= 0) {
                isRespawning = false;
                if(lives <= 0) {
                    gameState = GameState.GAME_OVER;
                } else {
                    player.setActive(true);
                }
            }
            return;
        }

        if (gameState == GameState.LEVEL_UP) {
            levelTransitionTimer += 0.016f;
            if (levelTransitionTimer >= TRANSITION_DELAY) {
                level++;
                levelTransitionTimer = 0;
                gameState = GameState.PLAYING;
                initGame(false);
            }
            return;
        }

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

        walls.removeIf(Wall::isDestroyed);

        // Collision detection
        checkCollisions();

        // Check win/lose conditions
        if (aliens.isEmpty() && gameState == GameState.PLAYING) {
            if (level >= MAX_LEVEL) {
                gameState = GameState.VICTORY;
            } else {
                gameState = GameState.LEVEL_UP;
            }
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
        swarmMoveTimer += 0.016f;

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
                swarmXSpeed *= 1.2f;  // Speed up
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

        // Alien vs Player collisions
        for (Alien alien : aliens) {
            if (!alien.isActive()) continue;

            if (alien.collidesWith(player)) {
                gameState = GameState.GAME_OVER;
                spawnExplosion(player.getX(), player.getY());
                break;
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
                spawnExplosion(player.getX(), player.getY());
                ab.setActive(false);
                lives--;

                isRespawning = true;
                respawnTimer = RESPAWN_DELAY;
                player.setActive(false); // Make player disappear during the pause
                bullets.clear();
                alienBullets.clear();
                break;
            }
        }

        // Player bullets vs Wall bricks
        for (Bullet b : bullets) {
            if (!b.isActive()) continue;
            for (Wall w : walls) {
                for (WallBricks br : w.getBricks()) {
                    if (!br.isActive()) continue;
                    if (br.isActive() && b.collidesWith(br)) {
                        b.setActive(false);
                        br.setActive(false);
                        spawnExplosion(br.getX(), br.getY());
                        break;
                    }
                }
            }
        }

        // Alien bullets vs Wall bricks
        for (AlienBullet ab : alienBullets) {
            if (!ab.isActive()) continue;
            for (Wall wall : walls) {
                for (WallBricks br : wall.getBricks()) {
                    if (!br.isActive()) continue;
                    if (br.isActive() && ab. collidesWith(br)) {
                        ab.setActive(false);
                        br.setActive(false);
                        spawnExplosion(br.getX(), br.getY());
                        break;
                    }
                }
            }
        }

        // Aliens vs Walls
        for (Alien a : aliens) {
            if (!a.isActive()) continue;
            for (Wall wall : walls) {
                for(WallBricks brick : wall.getBricks()) {
                    if (!brick.isActive()) continue;
                    if (brick.isActive() && a.collidesWith(brick)) {
                        brick.setActive(false);
                    }
                }
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

        // Render particles
        for (Particle particle : particles) {
            particle.display(gl);
        }

        // Render alien bullets
        for (AlienBullet ab : alienBullets) {
            ab.display(gl);
        }

        for (Wall wall : walls) {
            wall.display(gl);
        }

        // Render UI
        renderUI(gl);
    }

    private void renderUI(GL2 gl) {
        // Score display with text rendering
        textRenderer.beginRendering((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        textRenderer.setColor(Color.WHITE);

        // Draw actual Score text in the top-left
        textRenderer.draw("SCORE: " + score, 20, (int)SCREEN_HEIGHT - 40);

        // Draw current Level in the top-right
        String levelText = "LEVEL: " + level;
        Rectangle2D levelBounds = textRenderer.getBounds(levelText);
        textRenderer.draw(levelText, (int)(SCREEN_WIDTH - levelBounds.getWidth() - 20), (int)SCREEN_HEIGHT - 40);

        textRenderer.endRendering();

        // Game state messages
        if (gameState == GameState.GAME_OVER) {
            drawText("GAME OVER", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT * 0.80f, Color.RED);
        } else if (gameState == GameState.VICTORY) {
            drawText("MISSION ACCOMPLISHED", SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT * 0.80f, Color.GREEN);
        } else if (gameState == GameState.LEVEL_UP) {
            drawText("LEVEL " + level + " CLEARED", SCREEN_WIDTH / 2 - 180, SCREEN_HEIGHT * 0.80f, Color.CYAN);
        }

        float startX = 30.0f;
        float startY = 25.0f;
        float spacing = 35.0f;

        // Use half the default player size for the icons
        float miniWidth = Player.WIDTH / 2;
        float miniHeight = Player.HEIGHT / 2;

        for (int i = 0; i < lives; i++) {
            float xPos = startX + i * spacing;
            Player.displayNormalized(gl, xPos, startY, miniWidth, miniHeight);
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

    private void drawText(String text, float x, float y, Color color) {
        textRenderer.beginRendering((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        textRenderer.setColor(color);
        textRenderer.draw(text, (int)x, (int)y);
        textRenderer.endRendering();
    }
}
