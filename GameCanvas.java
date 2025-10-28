package org.example.akarnoidgame;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameCanvas extends Pane {
    // Enum để quản lý các trạng thái của game
    private enum GameState {
        MENU, PLAYING, GAMEOVER, YOUWIN
    }

    // Enum để quản lý các chế độ chơi
    public enum GameMode {
        POWER_UP, SPEED_RUN
    }

    // Các thành phần của game
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final List<Ball> balls = new ArrayList<>();
    private final List<Brick> bricks = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final Image background;

    // Các biến trạng thái của game
    private GameState gameState = GameState.MENU;
    private GameMode selectedGameMode;
    private int score = 0;
    private int lives = 5;
    private final Random random = new Random();
    private final double initialBallSpeed = 5;
    private double lastSpeedRunDx = initialBallSpeed;
    private double lastSpeedRunDy = -initialBallSpeed;

    // Biến theo dõi level
    private int currentLevel = 1;

    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image(getClass().getResource("image/background.png").toExternalForm());
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width, "/image/paddle.png");

        setupEventHandlers();
        setFocusTraversable(true);

        // Vòng lặp chính của game
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    private void setupEventHandlers() {
        // Xử lý sự kiện nhấn phím
        setOnKeyPressed(e -> {
            if (gameState == GameState.PLAYING) {
                // Di chuyển việc xử lý paddle vào đây
                paddle.handleKeyPress(e.getCode());

                // Xử lý phím SPACE để thả bóng
                if (e.getCode() == KeyCode.SPACE) {
                    balls.stream().filter(Ball::isStuck).forEach(ball -> {
                        if (selectedGameMode == GameMode.SPEED_RUN) {
                            ball.releaseBall(lastSpeedRunDx, -Math.abs(lastSpeedRunDy));
                        } else {
                            ball.releaseBall(initialBallSpeed, -initialBallSpeed);
                        }
                    });
                }
                // ✨ --- THÊM LOGIC PHÍM ESC --- ✨
                else if (e.getCode() == KeyCode.ESCAPE) {
                    // Dừng nhạc nền
                    GameMusic.getInstance().stopBackgroundMusic();
                    // Chuyển về màn hình Menu
                    gameState = GameState.MENU;
                    // Dọn dẹp các đối tượng game để sẵn sàng cho lần chơi mới
                    balls.clear();
                    bricks.clear();
                    powerUps.clear();
                    bullets.clear();
                }
                // ✨ --- KẾT THÚC --- ✨
            }
        });

        // Xử lý sự kiện thả phím
        setOnKeyReleased(e -> {
            // Chỉ xử lý thả phím nếu đang chơi
            if (gameState == GameState.PLAYING) {
                paddle.handleKeyRelease(e.getCode());
            }
        });

        // Xử lý sự kiện click chuột cho các trạng thái game
        setOnMouseClicked(e -> {
            switch (gameState) {
                case MENU:
                    // Click nút Power-Up
                    if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.POWER_UP;
                        startGame(selectedGameMode, 1);
                    }
                    // Click nút Speed Run
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.SPEED_RUN;
                        startGame(selectedGameMode, 1);
                    }
                    // ✨ --- THÊM LOGIC NÚT THOÁT --- ✨
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 120, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        Platform.exit(); // Lệnh thoát game chuẩn của JavaFX
                    }
                    // ✨ --- KẾT THÚC --- ✨
                    break;
                case GAMEOVER:
                case YOUWIN:
                    gameState = GameState.MENU;
                    break;
            }
        });
    }

    // Bắt đầu một màn chơi mới
    private void startGame(GameMode mode, int level) {
        this.selectedGameMode = mode;
        this.gameState = GameState.PLAYING;
        this.currentLevel = level; // Cập nhật level hiện tại
        lives = 7;
        // Chỉ reset điểm khi bắt đầu game mới (level 1)
        if (level == 1) {
            score = 0;
        }

        if (mode == GameMode.SPEED_RUN) {
            lastSpeedRunDx = initialBallSpeed;
            lastSpeedRunDy = -initialBallSpeed;
        }

        GameMusic.getInstance().playBackgroundMusic();
        setupGameObjects(canvas.getWidth(), level); // Truyền level vào
        resetBallToPaddle(); // Reset bóng cho mỗi level mới
        requestFocus();
    }

    // Thiết lập các đối tượng trong game theo layout
    private void setupGameObjects(double width, int level) {
        // Xóa các đối tượng của level cũ
        bricks.clear();
        powerUps.clear();
        balls.clear();
        bullets.clear();

        // Thêm bóng mới
        balls.add(new Ball(width / 2, 450, 25, width, canvas.getHeight(), "/image/ball.png"));

        // Tải layout gạch dựa trên level
        switch (level) {
            case 2:
                setupLayoutTriangle(width); // Level 2: Tam giác
                break;
            case 3:
                setupLayoutCorners(width); // Level 3: Bốn góc
                break;
            default: // Level 1
                setupLayoutClassic(width); // Level 1: Cổ điển
                break;
        }
    }

    // --- CÁC HÀM TẠO LAYOUT GẠCH ---

    private void setupLayoutClassic(double width) {
        int rows = 5;
        int cols = 10;
        double brickWidth = 60;
        double brickHeight = 25;
        double startX = 80;
        double startY = 80;
        double gapX = 20;
        double gapY = 15;
        String[] brickTypes = {"normal", "brick1", "brick2"};

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gapX);
                double y = startY + row * (brickHeight + gapY);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
    }

    private void setupLayoutTriangle(double width) {
        int numRows = 7;
        double brickWidth = 60;
        double brickHeight = 25;
        double gapX = 10;
        double gapY = 10;
        String[] brickTypes = {"normal", "brick1", "brick2"};

        for (int row = 0; row < numRows; row++) {
            int numBricksInRow = row + 1;
            double totalRowWidth = numBricksInRow * brickWidth + (numBricksInRow - 1) * gapX;
            double startX = (width - totalRowWidth) / 2;
            double y = 80 + row * (brickHeight + gapY);
            for (int col = 0; col < numBricksInRow; col++) {
                double x = startX + col * (brickWidth + gapX);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
    }

    private void setupLayoutCorners(double width) {
        int rowsPerCorner = 3;
        int colsPerCorner = 4;
        double brickWidth = 50;
        double brickHeight = 20;
        double gap = 10;

        // Góc trên bên trái
        createBrickRectangle(60, 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        // Góc trên bên phải
        createBrickRectangle(width - 60 - (colsPerCorner * (brickWidth + gap)), 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        // Góc dưới bên trái
        createBrickRectangle(60, 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        // Góc dưới bên phải
        createBrickRectangle(width - 60 - (colsPerCorner * (brickWidth + gap)), 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
    }

    // Hàm trợ giúp để tạo một khối gạch hình chữ nhật
    private void createBrickRectangle(double startX, double startY, int rows, int cols, double brickWidth, double brickHeight, double gap) {
        String[] brickTypes = {"normal", "brick1", "brick2"};
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gap);
                double y = startY + row * (brickHeight + gap);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
    }

    // Đưa bóng về lại thanh đỡ
    private void resetBallToPaddle() {
        // Chỉ xóa bóng cũ và tạo bóng mới
        balls.clear();

        Ball newBall = new Ball(paddle.getX() + paddle.getWidth() / 2, paddle.getY() - 25, 25, canvas.getWidth(), canvas.getHeight(), "/image/ball.png");
        newBall.setStuck(true);
        balls.add(newBall);
    }

    // Xử lý khi bóng rơi ra ngoài
    public void handleBallLost() {
        lives--; // Trừ mạng của người chơi

        if (lives <= 0) {
            // Nếu hết mạng, chuyển trạng thái và chỉ phát âm thanh Game Over
            gameState = GameState.GAMEOVER;
            GameMusic.getInstance().stopBackgroundMusic();
            GameMusic.getInstance().playGameOverSound(); // <-- Chỉ phát âm thanh này

            // Dọn dẹp các đối tượng trên màn hình
            powerUps.clear();
            bullets.clear();
            balls.clear();
        } else {
            // Nếu vẫn còn mạng, thì mới phát âm thanh mất mạng và reset bóng
            GameMusic.getInstance().playLoseLifeSound(); // <-- Di chuyển vào đây

            // Dọn dẹp power-up và đạn cũ, reset bóng
            powerUps.clear();
            bullets.clear();
            resetBallToPaddle();
        }
    }

    // --- LOGIC CẬP NHẬT GAME ---

    private void update() {
        if (gameState != GameState.PLAYING) return;

        paddle.update();

        // Cập nhật bóng
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            if (ball.isStuck()) {
                ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
                ball.setY(paddle.getY() - ball.getHeight());
            } else {
                ball.update();
            }

            // Xử lý bóng rơi
            if (ball.getY() > canvas.getHeight()) {
                if (balls.size() == 1 && selectedGameMode == GameMode.SPEED_RUN) {
                    lastSpeedRunDx = ball.getDx();
                    lastSpeedRunDy = ball.getDy();
                }
                ballIterator.remove();
            }

            // Va chạm bóng và thanh đỡ
            if (!ball.isStuck() && ball.intersects(paddle)) {
                GameMusic.getInstance().playPaddleHitSound();
                double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
                if (speed == 0) speed = initialBallSpeed;

                double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                double ballCenter = ball.getX() + ball.getWidth() / 2;
                double relativeIntersectX = ballCenter - paddleCenter;
                double normalizedIntersectX = relativeIntersectX / (paddle.getWidth() / 2);

                double newDirectionX = normalizedIntersectX;
                double newDirectionY = -1; // Always bounce up
                double length = Math.sqrt(newDirectionX * newDirectionX + newDirectionY * newDirectionY);
                if (length == 0) length = 1; // Avoid division by zero
                double normalizedDx = newDirectionX / length;
                double normalizedDy = newDirectionY / length;

                ball.setDx(normalizedDx * speed);
                ball.setDy(normalizedDy * speed);
                ball.setY(paddle.getY() - ball.getHeight()); // Reposition ball
            }

            // Va chạm bóng và gạch
            for (Brick b : bricks) {
                if (b.isVisible() && ball.intersects(b)) {
                    GameMusic.getInstance().playBrickBreakSound();
                    handleBrickCollision(ball, b);
                    if (b.hit()) { // Check if the brick was actually destroyed
                        score += (random.nextInt(21) + 10);
                        if (selectedGameMode == GameMode.POWER_UP) {
                            spawnPowerUp(b);
                        }
                    }
                    if (selectedGameMode == GameMode.SPEED_RUN) {
                        for (Ball currentBall : balls) {
                            currentBall.increaseSpeed(1.005); // Increase speed slightly
                        }
                    }
                    break; // Ball only hits one brick per update
                }
            }
        }

        if (balls.isEmpty() && gameState == GameState.PLAYING) {
            handleBallLost();
        }

        if (selectedGameMode == GameMode.POWER_UP) {
            updatePowerUpsAndBullets();
        }

        // Kiểm tra điều kiện thắng
        if (bricks.stream().noneMatch(brick -> !brick.isIndestructible() && brick.isVisible())) {

            GameMusic.getInstance().stopBackgroundMusic(); // Dừng nhạc nền

            if (currentLevel < 3) {
                // --- Thắng level 1 hoặc 2 -> Chuyển level ---
                currentLevel++;
                GameMusic.getInstance().playYouWinSound(); // Phát âm thanh "hoàn thành level"
                // Bắt đầu level tiếp theo
                startGame(selectedGameMode, currentLevel);
            } else {
                // --- Thắng level 3 -> Thắng toàn bộ game ---
                gameState = GameState.YOUWIN;
                GameMusic.getInstance().playYouWinSound(); // Phát âm thanh chiến thắng cuối cùng
            }
        }
    }

    // Xử lý va chạm giữa bóng và gạch
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
            // Vertical collision
            ball.bounceY();
            // Push ball out
            if (diffY > 0) ball.setY(brick.getY() + brick.getHeight());
            else ball.setY(brick.getY() - ball.getHeight());
        } else {
            // Horizontal collision
            ball.bounceX();
            // Push ball out
            if (diffX > 0) ball.setX(brick.getX() + brick.getWidth());
            else ball.setX(brick.getX() - ball.getWidth());
        }
    }

    // Cập nhật vật phẩm và đạn
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
            // Check bullet collision with bricks
            for (Brick brick : bricks) {
                if (brick.isVisible() && bullet.intersects(brick)) {
                    if (brick.hit()) { // Check if brick was destroyed
                        score += 25; // Add score for bullet hit
                    }
                    bulletIterator.remove(); // Remove bullet after hit
                    break; // Bullet hits only one brick
                }
            }
        }
    }

    // Tạo vật phẩm
    private void spawnPowerUp(Brick b) {
        if (random.nextDouble() < 0.35) { // 35% chance to spawn a power-up
            int type = random.nextInt(5); // 5 types of power-ups currently
            double x = b.getX() + b.getWidth() / 2 - 20; // Center the power-up horizontally
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

    // --- CÁC HÀM CÔNG KHAI CHO POWER-UP TƯƠNG TÁC ---
    public Paddle getPaddle() {
        return this.paddle;
    }

    public void addScore(int amount) {
        this.score += amount;
        if (this.score < 0) this.score = 0; // Prevent score from going below zero
    }

    public void multiplyScore(int factor) {
        this.score *= factor;
    }

    public void multiplyBalls() {
        // Giới hạn số lượng bóng tối đa để tránh lag (ví dụ: 4 quả)
        if (!balls.isEmpty() && balls.size() < 4) {
            // Tạo một danh sách tạm thời để chứa các quả bóng mới
            // Tránh lỗi ConcurrentModificationException khi duyệt và sửa danh sách cùng lúc
            List<Ball> newBalls = new ArrayList<>();

            for (Ball existingBall : balls) {
                // Tạo một quả bóng mới tại vị trí của quả bóng hiện có
                Ball newBall = new Ball(existingBall.getX(), existingBall.getY(), 25, canvas.getWidth(), canvas.getHeight(), "/image/ball.png");

                // Thả quả bóng mới với vận tốc x ngược lại và vận tốc y giữ nguyên
                newBall.releaseBall(-existingBall.getDx(), existingBall.getDy());

                newBalls.add(newBall);
            }

            // Thêm tất cả các quả bóng mới vào danh sách chính
            balls.addAll(newBalls);
        }
    }

    public void shootBullets() {
        // Lấy vị trí bắt đầu từ tâm của thanh đỡ
        double startX = paddle.getX() + paddle.getWidth() / 2;
        double startY = paddle.getY();

        int bulletCount = 10; // Số lượng đạn bắn ra mỗi lần
        double bulletSpacing = 25; // Khoảng cách giữa các viên đạn để tạo thành một luồng

        // Vòng lặp để tạo ra một chùm đạn
        for (int i = 0; i < bulletCount; i++) {
            // Tạo một viên đạn mới, với mỗi viên đạn sau được đặt cao hơn một chút
            // để chúng không xuất hiện cùng một lúc tại một điểm.
            bullets.add(new Bullet(startX, startY - (i * bulletSpacing)));
        }
    }

    // --- CÁC HÀM VẼ (RENDER) ---
    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());

        switch (gameState) {
            case PLAYING:
                paddle.render(gc);
                for (Brick b : bricks) b.render(gc);
                for (Ball ball : balls) ball.render(gc);
                if (selectedGameMode == GameMode.POWER_UP) {
                    for (PowerUp p : powerUps) p.render(gc);
                    for (Bullet bullet : bullets) bullet.render(gc);
                }
                renderUI(); // Vẽ UI
                break;
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

    // Hàm kiểm tra click vào nút
    private boolean isButtonClicked(double mouseX, double mouseY, double buttonY, double buttonWidth, double buttonHeight) {
        double buttonX = canvas.getWidth() / 2 - buttonWidth / 2;
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    // Vẽ màn hình Menu
    private void renderMenu() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("ARKANOID", canvas.getWidth() / 2, canvas.getHeight() / 3);

        // Nút Power-Up
        double btn1Y = canvas.getHeight() / 2 - 40;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn1Y, 280, 60, 20, 20);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText("Chế độ Power-Up", canvas.getWidth() / 2, btn1Y + 30);

        // Nút Speed Run
        double btn2Y = canvas.getHeight() / 2 + 40;
        gc.setFill(Color.ORANGERED);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn2Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Chế độ Tốc độ", canvas.getWidth() / 2, btn2Y + 30);

        double btn3Y = canvas.getHeight() / 2 + 120; // 40 (btn2) + 60 (height) + 20 (spacing)
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn3Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Thoát Game", canvas.getWidth() / 2, btn3Y + 30);
    }

    // Vẽ giao diện người chơi (Điểm, Mạng, Level)
    private void renderUI() {
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));

        // Hiển thị Điểm
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Điểm: " + score, 10, 30);

        // Hiển thị Mạng
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("Mạng: " + lives, canvas.getWidth() - 10, 30);

        // Hiển thị Level
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Level: " + currentLevel, canvas.getWidth() / 2, 30);
    }

    // Vẽ màn hình Game Over
    private void renderGameOver() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
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

    // Vẽ màn hình Chiến thắng
    private void renderYouWin() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
