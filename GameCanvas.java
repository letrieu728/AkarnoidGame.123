package org.example.akarnoidgame;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameCanvas extends Pane {
    /**
     * Enum to manage game states.
     */
    private enum GameState {
        MENU, PLAYING, GAMEOVER, YOUWIN
    }

    /**
     * NEW Enum to manage game modes.
     */
    public enum GameMode {
        POWER_UP, SPEED_RUN
    }

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Image background;

    private GameState gameState = GameState.MENU;
    private GameMode selectedGameMode; // NEW variable to store the selected game mode
    private int score = 0;
    private int lives = 5;
    private final Random random = new Random();
    private final double initialBallSpeed = 5;

    // --- NEW VARIABLE: Stores the last speed in Speed Run mode ---
    private double lastSpeedRunDx = initialBallSpeed;
    private double lastSpeedRunDy = -initialBallSpeed;


    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image(getClass().getResource("/image/background.png").toExternalForm());
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width, "/image/paddle.png");

        // MODIFIED: Ball launch logic based on game mode
        setOnKeyPressed(e -> {
            paddle.handleKeyPress(e.getCode());
            if (gameState == GameState.PLAYING && e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                balls.stream().filter(Ball::isStuck).forEach(ball -> {
                    if (selectedGameMode == GameMode.SPEED_RUN) {
                        // Launch with saved speed, ensuring it always goes up
                        ball.releaseBall(lastSpeedRunDx, -Math.abs(lastSpeedRunDy));
                    } else {
                        // Power-up mode uses initial speed
                        ball.releaseBall(initialBallSpeed, -initialBallSpeed);
                    }
                });
            }
        });

        setOnKeyReleased(e -> paddle.handleKeyRelease(e.getCode()));

        // MODIFIED: Handle mouse clicks for the mode selection menu
        setOnMouseClicked(e -> {
            if (gameState == GameState.MENU) {
                // Check for click on "Power-up Mode" button
                if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40)) {
                    startGame(GameMode.POWER_UP);
                }
                // Check for click on "Speed Run Mode" button
                else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40)) {
                    startGame(GameMode.SPEED_RUN);
                }
            } else if (gameState == GameState.GAMEOVER || gameState == GameState.YOUWIN) {
                // Return to menu on click to play again
                gameState = GameState.MENU;
            }
        });

        setFocusTraversable(true);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    /**
     * NEW METHOD: Starts the game with a specific mode.
     * @param mode The selected game mode (POWER_UP or SPEED_RUN).
     */
    private void startGame(GameMode mode) {
        this.selectedGameMode = mode;
        this.gameState = GameState.PLAYING;
        score = 0;
        lives = 5;
        // MODIFIED: Reset speed when starting a new Speed Run game
        if (mode == GameMode.SPEED_RUN) {
            lastSpeedRunDx = initialBallSpeed;
            lastSpeedRunDy = -initialBallSpeed;
        }
        // Reset game objects
        setupGameObjects(canvas.getWidth());
        resetBallToPaddle();
        requestFocus();
    }

    private void setupGameObjects(double width) {
        bricks.clear();
        powerUps.clear();
        balls.clear();
        bullets.clear();

        balls.add(new Ball(width / 2, 300, 25, width, canvas.getHeight(), "/image/ball.png"));

        int rows = 5;
        int cols = 10;
        double brickWidth = 60;
        double brickHeight = 25;
        double startX = 80;
        double startY = 80;
        double gapX = 20;
        double gapY = 15;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gapX);
                double y = startY + row * (brickHeight + gapY);
                bricks.add(new Brick(x, y, brickWidth, brickHeight, "/image/brick.png"));
            }
        }
    }

    private void resetBallToPaddle() {
        balls.clear();
        powerUps.clear();
        bullets.clear();
        Ball newBall = new Ball(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - 25, 25, canvas.getWidth(), canvas.getHeight(), "/image/ball.png");
        newBall.setStuck(true);
        balls.add(newBall);
    }

    public void handleBallLost() {
        lives--;
        if (lives <= 0) {
            gameState = GameState.GAMEOVER;
            powerUps.clear();
            bullets.clear();
            balls.clear();
        } else {
            resetBallToPaddle();
        }
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;

        paddle.update();

        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            if (ball.isStuck()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight());
            } else {
                ball.update();
            }

            // MODIFIED: Save speed before removing the ball
            if (ball.getY() > canvas.getHeight()) {
                // If this is the last ball in Speed Run mode, save its speed
                if (balls.size() == 1 && selectedGameMode == GameMode.SPEED_RUN) {
                    lastSpeedRunDx = ball.getDx();
                    lastSpeedRunDy = ball.getDy();
                }
                ballIterator.remove();
            }

            // ✨ --- IMPROVED PADDLE COLLISION --- ✨
            if (!ball.isStuck() && ball.intersects(paddle)) {
                ball.bounceY(); // Always bounce up vertically

                // Calculate collision position on the paddle (-1 to 1)
                double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                double ballCenter = ball.getX() + ball.getWidth() / 2;
                double relativeIntersectX = ballCenter - paddleCenter;
                double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);

                // Increase the factor for a stronger bounce at the edges
                double maxHorizontalSpeed = 7.5;
                ball.setDx(normalizedIntersectX * maxHorizontalSpeed);

                // Reposition ball just above the paddle to prevent getting stuck
                ball.setY(paddle.getY() - ball.getHeight());
            }

            // Brick collision logic based on game mode
            for (Brick b : bricks) {
                if (b.isVisible() && ball.intersects(b)) {

                    // ✨ --- IMPROVED BRICK COLLISION --- ✨
                    handleBrickCollision(ball, b);

                    b.setVisible(false);
                    score += (random.nextInt(21) + 10);

                    // Only spawn power-ups in POWER_UP mode
                    if (selectedGameMode == GameMode.POWER_UP) {
                        spawnPowerUp(b);
                    }
                    // Increase ball speed only in SPEED_RUN mode
                    else if (selectedGameMode == GameMode.SPEED_RUN) {
                        for (Ball currentBall : balls) {
                            currentBall.increaseSpeed(1.02); // Increase speed by 2%
                        }
                    }
                    break;
                }
            }
        }

        if (balls.isEmpty() && gameState == GameState.PLAYING) {
            handleBallLost();
        }

        // Only update power-ups and bullets in POWER_UP mode
        if (selectedGameMode == GameMode.POWER_UP) {
            updatePowerUpsAndBullets();
        }

        if (bricks.stream().noneMatch(Brick::isVisible)) {
            gameState = GameState.YOUWIN;
        }
    }

    /**
     * ✨ NEW METHOD: Handles smooth collision between ball and brick.
     * @param ball The ball
     * @param brick The brick
     */
    private void handleBrickCollision(Ball ball, Brick brick) {
        // Calculate centers and half-dimensions
        double ballCenterX = ball.getX() + ball.getWidth() / 2;
        double ballCenterY = ball.getY() + ball.getHeight() / 2;
        double brickCenterX = brick.getX() + brick.getWidth() / 2;
        double brickCenterY = brick.getY() + brick.getHeight() / 2;

        double combinedHalfWidth = ball.getWidth() / 2 + brick.getWidth() / 2;
        double combinedHalfHeight = ball.getHeight() / 2 + brick.getHeight() / 2;

        // Calculate distance between centers
        double diffX = ballCenterX - brickCenterX;
        double diffY = ballCenterY - brickCenterY;

        // Calculate overlap on each axis
        double overlapX = combinedHalfWidth - Math.abs(diffX);
        double overlapY = combinedHalfHeight - Math.abs(diffY);

        // Determine collision direction based on the smaller overlap
        if (overlapX >= overlapY) {
            // Vertical collision (top or bottom)
            ball.bounceY();
            // Push the ball out of the brick to prevent sticking
            if (diffY > 0) { // Ball is below the brick
                ball.setY(brick.getY() + brick.getHeight());
            } else { // Ball is above the brick
                ball.setY(brick.getY() - ball.getHeight());
            }
        } else {
            // Horizontal collision (left or right)
            ball.bounceX();
            // Push the ball out of the brick to prevent sticking
            if (diffX > 0) { // Ball is to the right of the brick
                ball.setX(brick.getX() + brick.getWidth());
            } else { // Ball is to the left of the brick
                ball.setX(brick.getX() - ball.getWidth());
            }
        }
    }

    /**
     * NEW METHOD: Separates the update logic for power-ups and bullets for clarity.
     */
    private void updatePowerUpsAndBullets() {
        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp p = powerUpIterator.next();
            p.update();
            if (p.intersects(paddle)) {
                applyPowerUpEffect(p);
                powerUpIterator.remove();
            } else if (p.getY() > canvas.getHeight()) {
                powerUpIterator.remove();
            }
        }

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            if (bullet.getY() + bullet.getHeight() < 0) {
                bulletIterator.remove();
                continue;
            }
            for (Brick brick : bricks) {
                if (brick.isVisible() && bullet.intersects(brick)) {
                    brick.setVisible(false);
                    score += 25;
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());

        if (gameState == GameState.PLAYING) {
            paddle.render(gc);
            for (Brick b : bricks) b.render(gc);
            for (Ball ball : balls) ball.render(gc);
            // Only render power-ups and bullets in POWER_UP mode
            if (selectedGameMode == GameMode.POWER_UP) {
                for (PowerUp p : powerUps) p.render(gc);
                for (Bullet bullet : bullets) bullet.render(gc);
            }
            renderUI();
        } else {
            // Other screens don't need to check game mode
            switch (gameState) {
                case MENU:
                    renderMenu();
                    break;
                case GAMEOVER:
                    renderGameOver();
                    break;
                case YOUWIN:
                    renderYouWin();
                    break;
            }
        }
    }

    /**
     * NEW METHOD: Checks for mouse click on a generic button.
     */
    private boolean isButtonClicked(double mouseX, double mouseY, double buttonY) {
        double buttonWidth = 280;
        double buttonHeight = 60;
        double buttonX = canvas.getWidth() / 2 - buttonWidth / 2;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    /**
     * MODIFIED: Renders the menu with two buttons to select game mode.
     */
    private void renderMenu() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ARKANOID", canvas.getWidth() / 2, canvas.getHeight() / 3);

        // Draw Button 1: Power-Up Mode
        double btn1Y = canvas.getHeight() / 2 - 40;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn1Y, 280, 60, 20, 20);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 24));
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Chế độ Power-Up", canvas.getWidth() / 2, btn1Y + 30);

        // Draw Button 2: Speed Run Mode
        double btn2Y = canvas.getHeight() / 2 + 40;
        gc.setFill(Color.ORANGERED);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn2Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Chế độ Tốc độ", canvas.getWidth() / 2, btn2Y + 30);
    }

    // ... Other methods (renderGameOver, renderYouWin, etc.) are unchanged ...
    private void applyPowerUpEffect(PowerUp p) {
        switch (p.getType()) {
            case TRUDIEM:
                score -= 50;
                if (score < 0) score = 0;
                break;
            case X2SCORE:
                score *= 2;
                break;
            case X2BALL:
                if (!balls.isEmpty() && balls.size() < 4) {
                    List<Ball> newBalls = new ArrayList<>();
                    for (Ball existingBall : balls) {
                        Ball newBall = new Ball(existingBall.getX(), existingBall.getY(), 25, canvas.getWidth(), canvas.getHeight(), "/image/ball.png");
                        newBall.releaseBall(-existingBall.getDx(), existingBall.getDy());
                        newBalls.add(newBall);
                    }
                    balls.addAll(newBalls);
                }
                break;
            case EXPAND:
                paddle.expand();
                break;
            case BULLET:
                shootBullets();
                break;
            case LASER:
                score += 100;
                break;
        }
    }
    private void spawnPowerUp(Brick b) {
        if (random.nextDouble() < 0.35) {
            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType randomType = types[random.nextInt(types.length)];
            double x = b.getX() + b.getWidth() / 2 - 20;
            double y = b.getY();
            powerUps.add(new PowerUp(x, y, 40, randomType));
        }
    }
    private void shootBullets() {
        double startX = paddle.getX() + paddle.getWidth() / 2;
        double startY = paddle.getY();
        int bulletCount = 10;
        double bulletSpacing = 25;

        for (int i = 0; i < bulletCount; i++) {
            bullets.add(new Bullet(startX, startY - (i * bulletSpacing)));
        }
    }
    private void renderUI() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Điểm: " + score, 10, 30);
        gc.fillText("Mạng: " + lives, canvas.getWidth() - 100, 30);
    }
    private void renderGameOver() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2 - 40);

        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm của bạn: " + score, canvas.getWidth() / 2, canvas.getHeight() / 2 + 20);

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", canvas.getWidth() / 2, canvas.getHeight() / 2 + 70);
    }
    private void renderYouWin() {
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("YOU WIN!", canvas.getWidth() / 2, canvas.getHeight() / 2 - 40);

        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm cuối cùng: " + score, canvas.getWidth() / 2, canvas.getHeight() / 2 + 20);

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", canvas.getWidth() / 2, canvas.getHeight() / 2 + 70);
    }
}

