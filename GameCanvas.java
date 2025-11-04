package org.example.akarnoidgame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

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
    // Enum quản lý các trạng thái của game
    private enum GameState {
        MENU, PLAYING, GAMEOVER, YOUWIN, HIGH_SCORE_SELECTION, HIGH_SCORE, PAUSED
    }

    // Enum quản lý các chế độ chơi
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
    private int currentLevel = 1;
    private final List<Integer> powerUpHighScores = new ArrayList<>();
    private final List<Integer> speedRunHighScores = new ArrayList<>();
    private static final String POWER_UP_SCORE_FILE = "highscore_powerup.txt";
    private static final String SPEED_RUN_SCORE_FILE = "highscore_speedrun.txt";
    private static final int MAX_HIGH_SCORES = 5;

    private List<Integer> scoresToDisplay;
    private String highScoreTitle;

    public GameCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        background = new Image(getClass().getResource("/image/background.png").toExternalForm());
        paddle = new Paddle(width / 2 - 75, height - 60, 150, 25, width, "/image/paddle.png");

        setupEventHandlers();
        setFocusTraversable(true);
        loadAllHighScores();

        // Vòng lặp RENDER chính (AnimationTimer)
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(); 
            }
        }.start();
    }

    private void setupEventHandlers() {
        // Xử lý khi nhấn phím
        setOnKeyPressed(e -> {
            if (gameState == GameState.PLAYING) {
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
                // THÊM LOGIC PHÍM ESC
                else if (e.getCode() == KeyCode.ESCAPE) {
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

        // Xử lý sự kiện thả phím
        setOnKeyReleased(e -> {
            if (gameState == GameState.PLAYING) {
                paddle.handleKeyRelease(e.getCode());
            }
        });

        // Xử lý click chuột cho các trạng thái game
        setOnMouseClicked(e -> {
            switch (gameState) {
                case MENU:
                    if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.POWER_UP;
                        startGame(selectedGameMode, 1);
                    }
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        selectedGameMode = GameMode.SPEED_RUN;
                        startGame(selectedGameMode, 1);
                    }
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 120, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        gameState = GameState.HIGH_SCORE_SELECTION;
                    }
                    // THÊM LOGIC NÚT THOÁT 
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 200, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        Platform.exit(); // Lệnh thoát game chuẩn của JavaFX
                    }
                    // KẾT THÚC
                    break;
                case HIGH_SCORE_SELECTION:
                    // Click nút "BXH Power-Up" (Y: -40)
                    if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 - 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        scoresToDisplay = powerUpHighScores;
                        highScoreTitle = "BXH (Power-Up)";
                        gameState = GameState.HIGH_SCORE;
                    }
                    // Click nút "BXH Speed Run" (Y: +40)
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 40, 280, 60)) {
                        GameMusic.getInstance().playButtonClickSound();
                        scoresToDisplay = speedRunHighScores;
                        highScoreTitle = "BXH (Tốc Độ)";
                        gameState = GameState.HIGH_SCORE;
                    }
                    // Click nút "Quay lại" (Y: +120)
                    else if (isButtonClicked(e.getX(), e.getY(), canvas.getHeight() / 2 + 120, 280, 60)) {
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

    // Bắt đầu game mới
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

    // Thiết lập các đối tượng trong game theo layout
    private void setupGameObjects(double width, int level) {
        bricks.clear();
        powerUps.clear();
        balls.clear();
        bullets.clear();

        balls.add(new Ball(width / 2, 450, 25, width, canvas.getHeight(), "/image/ball.png"));

        switch (level) {
            case 2:
                setupLayoutTriangle(width);
                break;
            case 3:
                setupLayoutCorners(width);
                break;
            default: // Level 1
                setupLayoutClassic(width);
                break;
        }
    }

    // CÁC HÀM TẠO LAYOUT GẠCH

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

        createBrickRectangle(60, 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(width - 60 - (colsPerCorner * (brickWidth + gap)), 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(60, 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(width - 60 - (colsPerCorner * (brickWidth + gap)), 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
    }

    // Hàm tạo một khối gạch hình chữ nhật
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
        balls.clear();

        // Đưa paddle về giữa màn hình
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

    // Khi bóng rơi ra ngoài
    public void handleBallLost() {
        lives--; // Trừ mạng của người chơi

        if (lives <= 0) {
            // Nếu hết mạng, chuyển trạng thái và chỉ phát âm thanh Game Over
            checkAndAddHighScore(score, selectedGameMode);
            gameState = GameState.GAMEOVER;
            GameMusic.getInstance().stopBackgroundMusic();
            GameMusic.getInstance().playGameOverSound(); // Chỉ phát âm thanh này

            // Dọn dẹp các đối tượng trên màn hình
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

  // LOGIC CẬP NHẬT GAME 

    private void update() {
        if (gameState == GameState.PAUSED) {
            return;
        }
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

            // Va chạm ball và paddle
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

            // Va chạm ball và gạch
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

        // Kiểm tra điều kiện thắng
        if (bricks.stream().noneMatch(brick -> !brick.isIndestructible() && brick.isVisible())) {
            GameMusic.getInstance().stopBackgroundMusic();

            if (currentLevel < 3) 
                currentLevel++;
                GameMusic.getInstance().playYouWinSound();
                startGame(selectedGameMode, currentLevel);
            } else {
                checkAndAddHighScore(score, selectedGameMode);
                gameState = GameState.YOUWIN;
                GameMusic.getInstance().playYouWinSound();
            }
        }
    }
    
    // Xử lý va chạm giữa bóng và gạch
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

    // Tạo vật phẩm
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

    // CÁC HÀM CÔNG KHAI CHO POWER-UP TƯƠNG TÁC
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
        // Giới hạn số lượng bóng tối đa để tránh lag (ví dụ: 4 quả)
        if (!balls.isEmpty()) {
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
            
            balls.addAll(newBalls);
        }
    }

    public void shootBullets() {
        // Lấy vị trí bắt đầu từ tâm của thanh đỡ
        double startX = paddle.getX() + paddle.getWidth() / 2;
        double startY = paddle.getY();

        int bulletCount = 10;
        double bulletSpacing = 25;

        // Vòng lặp để tạo ra một chùm đạn
        for (int i = 0; i < bulletCount; i++) {
            // Tạo một viên đạn mới, với mỗi viên đạn sau được đặt cao hơn một chút
            // để chúng không xuất hiện cùng một lúc tại một điểm.
            bullets.add(new Bullet(startX, startY - (i * bulletSpacing)));
        }
    }
    private void loadAllHighScores() {
        loadScoresFromFile(POWER_UP_SCORE_FILE, powerUpHighScores);
        loadScoresFromFile(SPEED_RUN_SCORE_FILE, speedRunHighScores);
    }

    //  Hàm trợ giúp để tải một file điểm cụ thể vào một danh sách cụ thể.
    private void loadScoresFromFile(String fileName, List<Integer> scoreList) {
        scoreList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scoreList.add(Integer.parseInt(line));
                } catch (NumberFormatException e) {
                    System.err.println("Bỏ qua dòng điểm không hợp lệ: " + line);
                }
            }
            scoreList.sort(Collections.reverseOrder());
        } catch (FileNotFoundException e) {
            System.out.println("Không tìm thấy file: " + fileName + ". Sẽ tạo file mới.");
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + fileName);
            e.printStackTrace();
        }
    }

    // Hàm trợ giúp để lưu một danh sách điểm vào một file cụ thể.
    private void saveScoresToFile(String fileName, List<Integer> scoreList) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int s : scoreList) {
                writer.println(s);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file: " + fileName);
        }
    }

    // Kiểm tra điểm số mới, thêm vào danh sách CHÍNH XÁC, và lưu lại file.
    private void checkAndAddHighScore(int newScore, GameMode mode) {
        // Chọn đúng danh sách và tên file dựa trên chế độ chơi
        List<Integer> targetList = (mode == GameMode.POWER_UP) ? powerUpHighScores : speedRunHighScores;
        String targetFile = (mode == GameMode.POWER_UP) ? POWER_UP_SCORE_FILE : SPEED_RUN_SCORE_FILE;

        targetList.add(newScore);
        targetList.sort(Collections.reverseOrder());

        // Giữ lại top 5
        while (targetList.size() > MAX_HIGH_SCORES) {
            targetList.remove(targetList.size() - 1);
        }

        saveScoresToFile(targetFile, targetList);
    }

        // CÁC HÀM VẼ (RENDER) 
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
                renderUI();
                break;
            case PAUSED:
                // 1. Vẽ lại khung hình game đang bị pause
                paddle.render(gc);
                for (Brick b : bricks) b.render(gc);
                for (Ball ball : balls) ball.render(gc);
                if (selectedGameMode == GameMode.POWER_UP) {
                    for (PowerUp p : powerUps) p.render(gc);
                    for (Bullet bullet : bullets) bullet.render(gc);
                }
                renderUI();
                renderPauseScreen();
                break;
            case MENU:
                renderMenu();
                break;
            case HIGH_SCORE_SELECTION: 
                renderHighScoreSelection();
                break;
            case HIGH_SCORE:
                renderHighScores();
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

        double btn3Y = canvas.getHeight() / 2 + 120;
        gc.setFill(Color.GOLD);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn3Y, 280, 60, 20, 20);
        gc.setFill(Color.BLACK);
        gc.fillText("Bảng Xếp Hạng", canvas.getWidth() / 2, btn3Y + 30);

        double btn4Y = canvas.getHeight() / 2 + 200; // 40 (btn2) + 60 (height) + 20 (spacing)
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn4Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Thoát Game", canvas.getWidth() / 2, btn4Y + 30);
    }
    private void renderHighScores() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText(highScoreTitle, canvas.getWidth() / 2, canvas.getHeight() / 4);

        gc.setFont(Font.font("Arial", 30));
        gc.setFill(Color.WHITE);

        double startY = canvas.getHeight() / 2 - 80;

        // Dùng danh sách đã chọn
        if (scoresToDisplay == null || scoresToDisplay.isEmpty()) {
            gc.fillText("Chưa có điểm nào", canvas.getWidth() / 2, startY);
        } else {
            for (int i = 0; i < scoresToDisplay.size(); i++) {
                gc.fillText((i + 1) + ".   " + scoresToDisplay.get(i), canvas.getWidth() / 2, startY + i * 40);
            }
        }

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để quay lại", canvas.getWidth() / 2, canvas.getHeight() - 100);
    }

    private void renderHighScoreSelection() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("CHỌN BẢNG XẾP HẠNG", canvas.getWidth() / 2, canvas.getHeight() / 3);

        gc.setFont(Font.font("Arial", 24));

        // Tận dụng nút từ menu (Y: -40)
        double btn1Y = canvas.getHeight() / 2 - 40;
        gc.setFill(Color.LIMEGREEN);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn1Y, 280, 60, 20, 20);
        gc.setFill(Color.BLACK);
        gc.fillText("BXH Power-Up", canvas.getWidth() / 2, btn1Y + 30);

        // Tận dụng nút từ menu (Y: +40)
        double btn2Y = canvas.getHeight() / 2 + 40;
        gc.setFill(Color.ORANGERED);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn2Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("BXH Tốc Độ", canvas.getWidth() / 2, btn2Y + 30);

        // Nút quay lại (Y: +120)
        double btn3Y = canvas.getHeight() / 2 + 120;
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(canvas.getWidth() / 2 - 140, btn3Y, 280, 60, 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText("Quay lại", canvas.getWidth() / 2, btn3Y + 30);
    }
    private void renderPauseScreen() {
        // Vẽ một lớp phủ màu đen mờ
        gc.setFill(Color.rgb(0, 0, 0, 0.5)); // 50% trong suốt
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Vẽ chữ "PAUSED"
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("PAUSED", canvas.getWidth() / 2, canvas.getHeight() / 2);

        // Vẽ hướng dẫn
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Nhấn 'P' để tiếp tục", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50);
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










