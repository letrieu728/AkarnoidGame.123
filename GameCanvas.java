package org.example.akarnoidgame;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameCanvas extends Pane {
    public enum GameState {
        MENU, PLAYING, GAMEOVER, YOUWIN, HIGH_SCORE_SELECTION, HIGH_SCORE, PAUSED
    }

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
    private final GameRenderer renderer;

    private GameState gameState = GameState.MENU;
    private GameMode selectedGameMode;
    private int score = 0;
    private int lives = 5;
    private final Random random = new Random();
    private final double initialBallSpeed = 5;
    private double lastSpeedRunDx = initialBallSpeed;
    private double lastSpeedRunDy = -initialBallSpeed;
    private int currentLevel = 1;
    private final HighScoreManager scoreManager;
    private final LevelManager levelManager;
    private List<Integer> scoresToDisplay;
    private String highScoreTitle;

    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        scoreManager = new HighScoreManager();
        levelManager = new LevelManager();
        background = new Image(getClass().getResource("/image/background.png").toExternalForm());
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width, "/image/paddle.png");
        renderer = new GameRenderer(gc, background);
        setupEventHandlers();
        setFocusTraversable(true);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }


    private void setupEventHandlers() {
        setOnKeyPressed(e -> {
            if (gameState == GameState.PLAYING) {
                paddle.handleKeyPress(e.getCode());

                if (e.getCode() == KeyCode.SPACE) {
                    balls.stream().filter(Ball::isStuck).forEach(ball -> {
                        if (selectedGameMode == GameMode.SPEED_RUN) {
                            ball.releaseBall(lastSpeedRunDx, -Math.abs(lastSpeedRunDy));
                        } else {
                            ball.releaseBall(initialBallSpeed, -initialBallSpeed);
                        }
                    });
                } else if (e.getCode() == KeyCode.ESCAPE) {
                    GameMusic.getInstance().stopBackgroundMusic();
                    gameState = GameState.MENU;
                    balls.clear();
                    bricks.clear();
                    powerUps.clear();
                    bullets.clear();
                }
            }
            if (e.getCode() == KeyCode.P) {
                if (gameState == GameState.PLAYING) {
                    gameState = GameState.PAUSED;
                    GameMusic.getInstance().pauseBackgroundMusic();
                } else if (gameState == GameState.PAUSED) {
                    gameState = GameState.PLAYING;
                    GameMusic.getInstance().resumeBackgroundMusic();
                }
            }
        });
        setOnKeyReleased(e -> {
            if (gameState == GameState.PLAYING) {
                paddle.handleKeyRelease(e.getCode());
            }
        });
        setOnMouseClicked(e -> {
            switch (gameState) {
                case MENU:
                    if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.POWER_UP;
                        startGame(selectedGameMode, 1);
                    } else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.SPEED_RUN;
                        startGame(selectedGameMode, 1);
                    } else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 120, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        gameState = GameState.HIGH_SCORE_SELECTION;
                    } else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 200, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        Platform.exit();
                    }
                    break;
                case HIGH_SCORE_SELECTION:

                    if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        scoresToDisplay = scoreManager.getPowerUpHighScores();
                        highScoreTitle = "BXH (Power-Up)";
                        gameState = GameState.HIGH_SCORE;
                    } else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        scoresToDisplay = scoreManager.getSpeedRunHighScores();
                        highScoreTitle = "BXH (Tốc Độ)";
                        gameState = GameState.HIGH_SCORE;
                    } else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 120, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        gameState = GameState.MENU;
                    }
                    break;
                case GAMEOVER:
                case YOUWIN:
                    gameState = GameState.MENU;
                    break;
                case HIGH_SCORE:
                    GameMusic.getInstance().playButtonClickSound();
                    gameState = GameState.MENU;
                    break;
            }
        });
    }

    // Bắt đầu một màn chơi mới
    private void startGame(GameMode mode, int level) {
        this.selectedGameMode = mode;
        this.gameState = GameState.PLAYING;
        this.currentLevel = level;
        lives = 7;
        if (level == 1) {
            score = 0;
        }

        if (mode == GameMode.SPEED_RUN) {
            lastSpeedRunDx = initialBallSpeed;
            lastSpeedRunDy = -initialBallSpeed;
        }

        GameMusic.getInstance().playBackgroundMusic();
        setupGameObjects(canvas.getWidth(), level);
        resetBallToPaddle();
        requestFocus();
    }

    private void setupGameObjects(double width, int level) {
        bricks.clear();
        powerUps.clear();
        balls.clear();
        bullets.clear();
        balls.add(new Ball(width / 2, 450, 25, width, canvas.getHeight(), "/image/ball.png"));
        bricks.addAll(levelManager.loadLevel(level, width));
    }

    private void resetBallToPaddle() {
        balls.clear();
        paddle.setX(canvas.getWidth() / 2 - paddle.getWidth() / 2);

        Ball newBall = new Ball(
                paddle.getX() + paddle.getWidth() / 2,
                paddle.getY() - 25,
                25,
                canvas.getWidth(),
                canvas.getHeight(),
                "/image/ball.png"
        );
        newBall.setStuck(true);
        balls.add(newBall);
    }

    public void handleBallLost() {
        lives--;

        if (lives <= 0) {
            scoreManager.checkAndAddHighScore(score, selectedGameMode);
            gameState = GameState.GAMEOVER;
            GameMusic.getInstance().stopBackgroundMusic();
            GameMusic.getInstance().playGameOverSound();

            powerUps.clear();
            bullets.clear();
            balls.clear();
        } else {
            GameMusic.getInstance().playLoseLifeSound();
            powerUps.clear();
            bullets.clear();
            resetBallToPaddle();
        }
    }

    // --- LOGIC CẬP NHẬT GAME ---
    private void update() {
        if (gameState == GameState.PAUSED) {
            return;
        }
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

            if (ball.getY() > canvas.getHeight()) {
                if (balls.size() == 1 && selectedGameMode == GameMode.SPEED_RUN) {
                    lastSpeedRunDx = ball.getDx();
                    lastSpeedRunDy = ball.getDy();
                }
                ballIterator.remove();
            }

            if (!ball.isStuck() && ball.intersects(paddle)) {
                GameMusic.getInstance().playPaddleHitSound();
                double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
                if (speed == 0) speed = initialBallSpeed;

                double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                double ballCenter = ball.getX() + ball.getWidth() / 2;
                double relativeIntersectX = ballCenter - paddleCenter;
                double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);

                double newDirectionX = normalizedIntersectX;
                double newDirectionY = -1;
                double length = Math.sqrt(newDirectionX * newDirectionX + newDirectionY * newDirectionY);
                if (length == 0) length = 1;
                double normalizedDx = newDirectionX / length;
                double normalizedDy = newDirectionY / length;

                ball.setDx(normalizedDx * speed);
                ball.setDy(normalizedDy * speed);
                ball.setY(paddle.getY() - ball.getHeight());
            }

            for (Brick b : bricks) {
                if (b.isVisible() && ball.intersects(b)) {
                    GameMusic.getInstance().playBrickBreakSound();
                    handleBrickCollision(ball, b);
                    if (b.hit()) {
                        score += (random.nextInt(21) + 10);
                        if (selectedGameMode == GameMode.POWER_UP) {
                            spawnPowerUp(b);
                        }
                    }
                    if (selectedGameMode == GameMode.SPEED_RUN) {
                        for (Ball currentBall : balls) {
                            currentBall.increaseSpeed(1.005);
                        }
                    }
                    break;
                }
            }
        }

        if (balls.isEmpty() && gameState == GameState.PLAYING) {
            handleBallLost();
        }

        if (selectedGameMode == GameMode.POWER_UP) {
            updatePowerUpsAndBullets();
        }

        if (bricks.stream().noneMatch(brick -> !brick.isIndestructible() && brick.isVisible())) {
            GameMusic.getInstance().stopBackgroundMusic();

            if (currentLevel < 3) {

                currentLevel++;
                GameMusic.getInstance().playYouWinSound();

                startGame(selectedGameMode, currentLevel);
            } else {
                scoreManager.checkAndAddHighScore(score, selectedGameMode);
                gameState = GameState.YOUWIN;
                GameMusic.getInstance().playYouWinSound();
            }
        }
    }

    private void handleBrickCollision(Ball ball, Brick brick) {

        double ballCenterX = ball.getX() + ball.getWidth() / 2;
        double ballCenterY = ball.getY() + ball.getHeight() / 2;
        double brickCenterX = brick.getX() + brick.getWidth() / 2;
        double brickCenterY = brick.getY() + brick.getHeight() / 2;

        double combinedHalfWidth = ball.getWidth() / 2 + brick.getWidth() / 2;
        double combinedHalfHeight = ball.getHeight() / 2 + brick.getHeight() / 2;

        double diffX = ballCenterX - brickCenterX;
        double diffY = ballCenterY - brickCenterY;

        double overlapX = combinedHalfWidth - Math.abs(diffX);
        double overlapY = combinedHalfHeight - Math.abs(diffY);

        if (overlapX >= overlapY) {
            ball.bounceY();
            if (diffY > 0) ball.setY(brick.getY() + brick.getHeight());
            else ball.setY(brick.getY() - ball.getHeight());
        } else {

            ball.bounceX();
            if (diffX > 0) ball.setX(brick.getX() + brick.getWidth());
            else ball.setX(brick.getX() - ball.getWidth());
        }
    }


    private void updatePowerUpsAndBullets() {
        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp p = powerUpIterator.next();
            p.update();
            if (p.intersects(paddle)) {
                GameMusic.getInstance().playPowerUpSound();
                p.applyEffect(this);
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
                    if (brick.hit()) {
                        score += 25;
                    }
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }

    private void spawnPowerUp(Brick b) {
        if (random.nextDouble() < 0.35) {
            int type = random.nextInt(5);
            double x = b.getX() + b.getWidth() / 2 - 20;
            double y = b.getY();

            PowerUp newPowerUp = switch (type) {
                case 0 -> new X2BallPowerUp(x, y);
                case 1 -> new PaddleExpandPowerUp(x, y);
                case 2 -> new BulletPowerUp(x, y);
                case 3 -> new X2ScorePowerUp(x, y);
                default -> new TruDiemPowerUp(x, y);
            };
            powerUps.add(newPowerUp);
        }
    }

    public Paddle getPaddle() {
        return this.paddle;
    }

    public void addScore(int amount) {
        this.score += amount;
        if (this.score < 0) this.score = 0;
    }

    public void multiplyScore(int factor) {
        this.score *= factor;
    }

    public void multiplyBalls() {
        if (!balls.isEmpty()) {
            List<Ball> newBalls = new ArrayList<>();

            for (Ball existingBall : balls) {
                Ball newBall = new Ball(existingBall.getX(), existingBall.getY(), 25, canvas.getWidth(), canvas.getHeight(), "/image/ball.png");
                newBall.releaseBall(-existingBall.getDx(), existingBall.getDy());

                newBalls.add(newBall);
            }
            balls.addAll(newBalls);
        }
    }

    public void shootBullets() {
        double startX = paddle.getX() + paddle.getWidth() / 2;
        double startY = paddle.getY();

        int bulletCount = 10;
        double bulletSpacing = 25;

        for (int i = 0; i < bulletCount; i++) {
            bullets.add(new Bullet(startX, startY - (i * bulletSpacing)));
        }
    }

    // --- CÁC HÀM VẼ (RENDER) ---
    private void render() {
        renderer.render(
                gameState,
                selectedGameMode,
                paddle,
                bricks,
                balls,
                powerUps,
                bullets,
                score,
                lives,
                currentLevel,
                canvas.getWidth(),
                canvas.getHeight(),
                scoresToDisplay,
                highScoreTitle
        );
    }

    private boolean isButtonClicked(double mouseX, double mouseY, double buttonY, double buttonWidth, double buttonHeight) {
        double buttonX = canvas.getWidth() / 2 - buttonWidth / 2;
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }
}
