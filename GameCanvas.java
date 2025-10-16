package org.example.akarnoidgame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameCanvas extends Pane {
    private enum GameState {
        MENU, PLAYING, GAMEOVER, YOUWIN
    }

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    // --- MỚI: Danh sách quản lý đạn ---
    private final List<Bullet> bullets = new ArrayList<>();
    private final Image background;

    private GameState gameState = GameState.MENU;
    private int score = 0;
    private int lives = 5;
    private final Random random = new Random();
    private final double initialBallSpeed = 5;


    public GameCanvas(double width, double height) {
        // ... (Hàm khởi tạo không đổi, chỉ cần đảm bảo các danh sách được khởi tạo)
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image(getClass().getResource("/image/background.png").toExternalForm());

        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width,
                "/image/paddle.png");

        setupGameObjects(width);
        resetBallToPaddle();

        setOnKeyPressed(e -> {
            paddle.handleKeyPress(e.getCode());
            if (gameState == GameState.PLAYING && e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                balls.stream()
                        .filter(Ball::isStuck)
                        .forEach(ball -> ball.releaseBall(initialBallSpeed, -initialBallSpeed));
            }
        });

        setOnKeyReleased(e -> paddle.handleKeyRelease(e.getCode()));

        setOnMouseClicked(e -> {
            if (gameState == GameState.MENU && isStartButtonClicked(e.getX(), e.getY())) {
                startGame();
            } else if (gameState == GameState.GAMEOVER || gameState == GameState.YOUWIN) {
                gameState = GameState.MENU;
                setupGameObjects(width);
                resetBallToPaddle();
                score = 0;
                lives = 5;
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

    private void setupGameObjects(double width) {
        bricks.clear();
        powerUps.clear();
        balls.clear();
        bullets.clear(); // Xóa đạn cũ khi thiết lập game mới

        balls.add(new Ball(width / 2, 300, 25, width, canvas.getHeight(),
                "/image/ball.png"));

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
                bricks.add(new Brick(x, y, brickWidth, brickHeight,
                        "/image/brick.png"));
            }
        }
    }

    private void resetBallToPaddle() {
        balls.clear();
        bullets.clear(); // Xóa đạn cũ khi reset
        Ball newBall = new Ball(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - 25, 25, canvas.getWidth(), canvas.getHeight(),
                "/image/ball.png");
        newBall.setStuck(true);
        balls.add(newBall);
    }

    // --- HÀM MỚI: Bắn ra một loạt đạn ---
    private void shootBullets() {
        double startX = paddle.getX() + paddle.getWidth() / 2;
        double startY = paddle.getY();
        int bulletCount = 3;
        double bulletSpacing = 25; // Khoảng cách giữa các viên đạn để tạo thành một luồng

        for (int i = 0; i < bulletCount; i++) {
            bullets.add(new Bullet(startX, startY - (i * bulletSpacing)));
        }
    }

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
            case BULLET: // <-- SỬA LẠI CASE BULLET
                shootBullets(); // Gọi hàm bắn đạn
                break;
            case LASER:
                score += 100; // Tạm thời cộng điểm
                break;
        }
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;

        paddle.update();

        // ... (Code cập nhật bóng và power-up không đổi) ...
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
                ballIterator.remove();
            }

            if (!ball.isStuck() && ball.intersects(paddle)) {
                ball.bounceY();
                double relativeIntersectX = (ball.getX() + ball.getWidth() / 2) - (paddle.getX() + paddle.getWidth() / 2);
                double normalizedRelativeIntersectX = relativeIntersectX / (paddle.getWidth() / 2);
                ball.setDx(normalizedRelativeIntersectX * 5);
                ball.setY(paddle.getY() - ball.getHeight());
            }

            List<Brick> visibleBricks = bricks.stream().filter(Brick::isVisible).collect(Collectors.toList());
            for (Brick b : visibleBricks) {
                if (ball.intersects(b)) {
                    b.setVisible(false);
                    score += (random.nextInt(21) + 10);
                    ball.bounceY();
                    spawnPowerUp(b);
                    break;
                }
            }
        }

        if (balls.isEmpty()) {
            handleBallLost();
        }

        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp p = powerUpIterator.next();
            p.update();

            if (p.intersects(paddle)) {
                applyPowerUpEffect(p);
                powerUpIterator.remove();
            }
            else if (p.getY() > canvas.getHeight()) {
                powerUpIterator.remove();
            }
        }

        // --- MỚI: Cập nhật và xử lý va chạm cho đạn ---
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();

            // Xóa đạn nếu bay ra khỏi màn hình
            if (bullet.getY() + bullet.getHeight() < 0) {
                bulletIterator.remove();
                continue; // Chuyển sang viên đạn tiếp theo
            }

            // Kiểm tra va chạm với gạch
            for (Brick brick : bricks) {
                if (brick.isVisible() && bullet.intersects(brick)) {
                    brick.setVisible(false);
                    score += 25; // Cộng điểm khi đạn trúng gạch
                    bulletIterator.remove(); // Xóa viên đạn
                    break; // Một viên đạn chỉ phá một viên gạch
                }
            }
        }

        if (bricks.stream().noneMatch(Brick::isVisible)) {
            gameState = GameState.YOUWIN;
        }
    }

    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());

        if (gameState == GameState.PLAYING) {
            paddle.render(gc);
            for (Brick b : bricks) b.render(gc);
            for (Ball ball : balls) ball.render(gc);
            for (PowerUp p : powerUps) p.render(gc);
            for (Bullet bullet : bullets) bullet.render(gc); // <-- VẼ ĐẠN
            renderUI();
        }

        // ... (Phần còn lại của hàm render không đổi) ...
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
            default:
                break;
        }
    }

    // ... (Tất cả các hàm còn lại không thay đổi)
    private void spawnPowerUp(Brick brick) {
        if (random.nextDouble() < 0.35) {
            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType randomType = types[random.nextInt(types.length)];
            double x = brick.getX() + brick.getWidth() / 2 - 20;
            double y = brick.getY();
            powerUps.add(new PowerUp(x, y, 40, randomType));
        }
    }

    public void handleBallLost() {
        lives--;
        if (lives <= 0) {
            gameState = GameState.GAMEOVER;
        } else {
            resetBallToPaddle();
        }
    }

    private void startGame() {
        gameState = GameState.PLAYING;
        requestFocus();
    }

    private boolean isStartButtonClicked(double mouseX, double mouseY) {
        double buttonX = canvas.getWidth() / 2 - 80;
        double buttonY = canvas.getHeight() / 2;
        double buttonWidth = 160;
        double buttonHeight = 50;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    private void renderUI() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Điểm: " + score, 10, 30);
        gc.fillText("Mạng: " + lives, canvas.getWidth() - 100, 30);
    }

    private void renderMenu() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText("ARKANOID", canvas.getWidth() / 2 - 120, canvas.getHeight() / 3);

        gc.setFill(Color.GREEN);
        gc.fillRect(canvas.getWidth() / 2 - 80, canvas.getHeight() / 2, 160, 50);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("START GAME", canvas.getWidth() / 2 - 55, canvas.getHeight() / 2 + 30);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16));
        gc.fillText("Nhấn SPACE để bắn bóng",
                canvas.getWidth() / 2 - 90, canvas.getHeight() / 2 + 80);
    }

    private void renderGameOver() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.fillText("GAME OVER", canvas.getWidth() / 2 - 180, canvas.getHeight() / 2 - 40);

        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm của bạn: " + score, canvas.getWidth() / 2 - 130, canvas.getHeight() / 2 + 20);

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", canvas.getWidth() / 2 - 70, canvas.getHeight() / 2 + 70);
    }

    private void renderYouWin() {
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 60));
        gc.fillText("YOU WIN!", canvas.getWidth() / 2 - 140, canvas.getHeight() / 2 - 40);

        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm cuối cùng: " + score, canvas.getWidth() / 2 - 150, canvas.getHeight() / 2 + 20);

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", canvas.getWidth() / 2 - 70, canvas.getHeight() / 2 + 70);
    }
}