package game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class GameCanvas extends Canvas {
    private GraphicsContext gc;
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private AnimationTimer gameLoop;
    private double screenWidth;
    private double screenHeight;

    public GameCanvas(double width, double height) {
        super(width, height);
        this.gc = getGraphicsContext2D();
        this.screenWidth = width;
        this.screenHeight = height;

        setupGame();
        setupInput();
        startGameLoop();
    }

    private void setupGame() {
        paddle = new Paddle(screenWidth / 2 - 50, screenHeight - 50, 100, 20, screenWidth);
        ball = new Ball(screenWidth / 2, screenHeight / 2, 15);
        bricks = new ArrayList<>();

        // Tạo lưới gạch (5 hàng, 10 cột)
        int rows = 5, cols = 10;
        double brickWidth = screenWidth / cols;
        double brickHeight = 25;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                bricks.add(new Brick(c * brickWidth, r * brickHeight + 50, brickWidth - 5, brickHeight - 5));
            }
        }

        ball.deactivate();
        ball.resetPosition(paddle.getX(), paddle.getY(), paddle.getWidth());
    }

    private void setupInput() {
        setFocusTraversable(true);

        // --- Bắt phím ---
        setOnKeyPressed((KeyEvent e) -> {
            KeyCode code = e.getCode();
            switch (code) {
                case LEFT -> leftPressed = true;
                case RIGHT -> rightPressed = true;
                case SPACE -> ball.activate();
            }
        });

        setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
        });

        // --- Bắt chuột ---
        setOnMouseClicked((MouseEvent e) -> {
            if (!ball.isActive()) ball.activate();
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // Paddle movement
        if (leftPressed) paddle.moveLeft();
        else if (rightPressed) paddle.moveRight();
        else paddle.stop();

        paddle.update();
        ball.update();

        if (!ball.isActive()) {
            ball.resetPosition(paddle.getX(), paddle.getY(), paddle.getWidth());
        }

        // Collision with walls
        ball.handleWallCollision(screenWidth, screenHeight);

        // Collision with paddle
        if (ball.collidesWith(paddle)) {
            double hitPos = paddle.getHitPosition(ball.getX() + ball.getWidth() / 2);
            ball.bounceFromPaddle(hitPos);
        }

        // Collision with bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.intersects(brick)) {
                ball.handleBrickCollision(brick);
                brick.hit();
            }
        }

        // Check lose condition
        if (ball.getY() > screenHeight) {
            ball.deactivate();
            ball.resetPosition(paddle.getX(), paddle.getY(), paddle.getWidth());
        }
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // Draw paddle
        paddle.render(gc);

        // Draw ball
        ball.render(gc);

        // Draw bricks
        for (Brick brick : bricks) {
            brick.render(gc);
        }

        // Instruction
        gc.setFill(Color.WHITE);
        gc.fillText("Press SPACE to start", 10, screenHeight - 10);
    }
}
