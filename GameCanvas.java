package game;

import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class GameCanvas extends Pane {

    private enum GameState { MENU, PLAYING, GAMEOVER, YOUWIN }
    public enum GameMode { POWER_UP, SPEED_RUN }

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Image background;

    private GameState gameState = GameState.MENU;
    private GameMode selectedGameMode;
    private int score = 0;
    private int lives = 5;
    private final Random random = new Random();
    private final double initialBallSpeed = 5;

    private double lastSpeedRunDx = initialBallSpeed;
    private double lastSpeedRunDy = -initialBallSpeed;

    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image(getClass().getResource("/image/background.png").toExternalForm());
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width, "/image/paddle.png");

        setOnKeyPressed(e -> {
            paddle.handleKeyPress(e.getCode());
            if (gameState == GameState.PLAYING && e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                balls.stream().filter(Ball::isStuck).forEach(ball -> {
                    if (selectedGameMode == GameMode.SPEED_RUN)
                        ball.releaseBall(lastSpeedRunDx, -Math.abs(lastSpeedRunDy));
                    else
                        ball.releaseBall(initialBallSpeed, -initialBallSpeed);
                });
            }
        });

        setOnKeyReleased(e -> paddle.handleKeyRelease(e.getCode()));

        setOnMouseClicked(e -> {
            if (gameState == GameState.MENU) {
                if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40))
                    startGame(GameMode.POWER_UP);
                else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40))
                    startGame(GameMode.SPEED_RUN);
            } else if (gameState == GameState.GAMEOVER || gameState == GameState.YOUWIN) {
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

    private void startGame(GameMode mode) {
        this.selectedGameMode = mode;
        this.gameState = GameState.PLAYING;
        score = 0;
        lives = 5;
        lastSpeedRunDx = initialBallSpeed;
        lastSpeedRunDy = -initialBallSpeed;
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

        int rows = 5, cols = 10;
        double brickWidth = 60, brickHeight = 25, startX = 80, startY = 80, gapX = 20, gapY = 15;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gapX);
                double y = startY + row * (brickHeight + gapY);
                String type;
                double rand = random.nextDouble();
                if (rand < 0.1) type = "indestructible";
                else if (rand < 0.3) type = "hard";
                else type = "normal";
                bricks.add(new Brick(x, y, brickWidth, brickHeight, type));
            }
        }
    }

    private void resetBallToPaddle() {
        balls.clear();
        powerUps.clear();
        bullets.clear();
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

    private void update() {
        if (gameState != GameState.PLAYING) return;

        paddle.update();

        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            if (ball.isStuck()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight());
            } else ball.update();

            if (ball.getY() > canvas.getHeight()) {
                if (balls.size() == 1 && selectedGameMode == GameMode.SPEED_RUN) {
                    lastSpeedRunDx = ball.getDx();
                    lastSpeedRunDy = ball.getDy();
                }
                iterator.remove();
            }

            if (!ball.isStuck() && ball.intersects(paddle)) {
                ball.bounceY();
                double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                double ballCenter = ball.getX() + ball.getWidth() / 2;
                double normalizedX = (ballCenter - paddleCenter) / (paddle.getWidth() / 2);
                ball.setDx(normalizedX * 7.5);
                ball.setY(paddle.getY() - ball.getHeight());
            }

            for (Brick b : bricks) {
                if (b.isVisible() && ball.intersects(b)) {
                    handleBrickCollision(ball, b);
                    boolean destroyed = b.hit();
                    if (destroyed) {
                        score += (random.nextInt(21) + 10);
                        if (selectedGameMode == GameMode.POWER_UP) spawnPowerUp(b);
                        else if (selectedGameMode == GameMode.SPEED_RUN)
                            for (Ball currentBall : balls) currentBall.increaseSpeed(1.02);
                    }
                    break;
                }
            }
        }

        if (balls.isEmpty()) handleBallLost();

        if (bricks.stream().noneMatch(Brick::isVisible))
            gameState = GameState.YOUWIN;
    }

    private void handleBallLost() {
        lives--;
        if (lives <= 0) {
            gameState = GameState.GAMEOVER;
            balls.clear();
            powerUps.clear();
            bullets.clear();
        } else resetBallToPaddle();
    }

    private void handleBrickCollision(Ball ball, Brick brick) {
        double ballCenterX = ball.getX() + ball.getWidth() / 2;
        double ballCenterY = ball.getY() + ball.getHeight() / 2;
        double brickCenterX = brick.getX() + brick.getWidth() / 2;
        double brickCenterY = brick.getY() + brick.getHeight() / 2;

        double overlapX = (ball.getWidth() / 2 + brick.getWidth() / 2) - Math.abs(ballCenterX - brickCenterX);
        double overlapY = (ball.getHeight() / 2 + brick.getHeight() / 2) - Math.abs(ballCenterY - brickCenterY);

        if (overlapX >= overlapY) {
            ball.bounceY();
            if (ballCenterY > brickCenterY) ball.setY(brick.getY() + brick.getHeight());
            else ball.setY(brick.getY() - ball.getHeight());
        } else {
            ball.bounceX();
            if (ballCenterX > brickCenterX) ball.setX(brick.getX() + brick.getWidth());
            else ball.setX(brick.getX() - ball.getWidth());
        }
    }

    private boolean isButtonClicked(double mouseX, double mouseY, double buttonY) {
        double buttonWidth = 280, buttonHeight = 60;
        double buttonX = canvas.getWidth() / 2 - buttonWidth / 2;
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        switch (gameState) {
            case MENU -> renderMenu();
            case PLAYING -> {
                paddle.render(gc);
                for (Brick b : bricks) b.render(gc);
                for (Ball ball : balls) ball.render(gc);
                renderUI();
            }
            case GAMEOVER -> renderGameOver();
            case YOUWIN -> renderYouWin();
        }
    }

    private void renderMenu() {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ARKANOID", canvas.getWidth() / 2, canvas.getHeight() / 3);
        double btn1Y = canvas.getHeight() / 2 - 40;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn1Y, 280, 60, 20, 20);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 24));
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Chế độ Power-Up", canvas.getWidth() / 2, btn1Y + 30);
        double btn2Y = canvas.getHeight() / 2 + 40;
        gc.setFill(Color.ORANGERED);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn2Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Chế độ Tốc độ", canvas.getWidth() / 2, btn2Y + 30);
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

    private void renderUI() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Điểm: " + score, 10, 30);
        gc.fillText("Mạng: " + lives, canvas.getWidth() - 120, 30);
    }

    private void renderGameOver() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GAME OVER", canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    private void renderYouWin() {
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("YOU WIN!", canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
}
