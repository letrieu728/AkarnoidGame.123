package org.example.akarnoidgame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp GameCanvas chịu trách nhiệm cập nhật và vẽ game Arkanoid.
 */
public class GameCanvas extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final Ball ball;
    private final List<Brick> bricks = new ArrayList<>();
    private final Image background;

    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Load nền
        background = new Image(getClass().getResource("/image/background.png").toExternalForm());

        // Tạo paddle, ball, brick
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width,
                getClass().getResource("/image/paddle.png").toExternalForm());
        ball = new Ball(width / 2, height / 2, 25, width, height,
                getClass().getResource("/image/ball.png").toExternalForm());

        int rows = 3;
        int cols = 6; // ✅ thêm số cột
        double brickWidth = 100;
        double brickHeight = 35;
        double startX = 80;
        double startY = 80;
        double gapX = 20;  // khoảng cách ngang giữa các brick
        double gapY = 15;  // khoảng cách dọc giữa các hàng

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gapX);
                double y = startY + row * (brickHeight + gapY);
                bricks.add(new Brick(x, y, brickWidth, brickHeight,
                        getClass().getResource("/image/brick.png").toExternalForm()));
            }
        }

        // ✅ Điều khiển bằng phím
        setOnKeyPressed(e -> paddle.handleKeyPress(e.getCode()));
        setOnKeyReleased(e -> paddle.handleKeyRelease(e.getCode()));
        setFocusTraversable(true);

        // ✅ Game loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    private void update() {
        paddle.update();
        ball.update();

        // Va chạm paddle
        if (ball.intersects(paddle)) {
            ball.bounce();
            ball.setY(paddle.getY() - ball.getHeight());
        }

        // Va chạm brick
        for (Brick b : bricks) {
            if (b.isVisible() && ball.intersects(b)) {
                b.setVisible(false);
                ball.bounce();
                break;
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        paddle.render(gc);
        ball.render(gc);
        for (Brick b : bricks) b.render(gc);
    }
}
