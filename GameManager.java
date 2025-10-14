package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Brick> bricks;
    private List<Ball> balls;
    private Paddle paddle;
    private double screenWidth;
    private double screenHeight;

    private int score;
    private int lives;
    private int maxLives;
    private boolean gameOver;
    private GameState gameState;
    private boolean levelCompleted;
    private Ball mainBall; // Lưu reference đến ball chính

    public enum GameState {
        MENU,
        PLAYING,
        GAME_OVER,
        LEVEL_COMPLETE,
        WAITING_FOR_START
    }

    public GameManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
        this.balls = new ArrayList<>();
        this.score = 0;
        this.maxLives = 3;
        this.lives = maxLives;
        this.gameOver = false;
        this.gameState = GameState.MENU;
        this.levelCompleted = false;
        createBricks();
    }

    // ========== INITIALIZATION METHODS ==========

    public void initializePaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public void initializeMainBall(Ball ball) {
        this.mainBall = ball;
        this.balls.clear();
        this.balls.add(ball);
        ball.completeReset();
    }

    // ========== GAME STATE MANAGEMENT ==========

    public void startGame(Ball ball, Paddle paddle) {
        gameState = GameState.WAITING_FOR_START;
        resetGame();
        resetBallPosition(ball, paddle);

        // Đảm bảo ball có trong danh sách
        if (!balls.contains(ball)) {
            balls.add(ball);
        }
    }

    public void resetGame() {
        createBricks();
        score = 0;
        lives = maxLives;
        gameOver = false;
        levelCompleted = false;
        gameState = GameState.WAITING_FOR_START;

        // Đảm bảo luôn có ball trong danh sách
        if (balls.isEmpty() && mainBall != null) {
            balls.add(mainBall);
        }

        // Reset tất cả balls
        for (Ball ball : balls) {
            ball.completeReset();
        }

        // Chỉ giữ lại ball chính nếu có nhiều ball
        if (balls.size() > 1 && mainBall != null) {
            balls.clear();
            balls.add(mainBall);
            mainBall.completeReset();
        }
    }

    public void returnToMenu() {
        gameState = GameState.MENU;
    }

    public void completeLevel() {
        gameState = GameState.LEVEL_COMPLETE;
        levelCompleted = true;
    }

    // ========== BALL MANAGEMENT ==========

    public void handleGameSpacePress(Ball ball, Paddle paddle) {
        if (gameState == GameState.WAITING_FOR_START) {
            ball.activate();
            gameState = GameState.PLAYING;
        } else if (gameState == GameState.PLAYING && !ball.isActive()) {
            ball.activate();
        }
    }

    public boolean handleBallFall(Ball ball, Paddle paddle) {
        lives--;
        if (lives <= 0) {
            gameOver = true;
            gameState = GameState.GAME_OVER;
            return true;
        } else {
            ball.completeReset();
            resetBallPosition(ball, paddle);
            gameState = GameState.WAITING_FOR_START;
            return false;
        }
    }

    public void resetBallPosition(Ball ball, Paddle paddle) {
        if (paddle == null || ball == null) return;

        double ballX = paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2;
        double ballY = paddle.getY() - ball.getHeight();
        ball.setPosition(ballX, ballY);
    }

    // ========== INPUT HANDLING ==========

    public void handleGameInput(javafx.scene.input.KeyCode code, Ball ball, Paddle paddle) {
        switch (code) {
            case R:
                resetGame();
                if (ball != null) {
                    resetBallPosition(ball, paddle);
                }
                break;
            case ESCAPE:
                returnToMenu();
                break;
            case SPACE:
                handleGameSpacePress(ball, paddle);
                break;
        }
    }

    public void handleMouseClick(double mouseX, double mouseY, Ball ball, Paddle paddle) {
        if (gameState == GameState.MENU) {
            if (isStartButtonClicked(mouseX, mouseY)) {
                startGame(ball, paddle);
            }
        } else if (gameState == GameState.GAME_OVER || gameState == GameState.LEVEL_COMPLETE) {
            if (isRestartButtonClicked(mouseX, mouseY)) {
                resetGame();
                resetBallPosition(ball, paddle);
            } else if (isMenuButtonClicked(mouseX, mouseY)) {
                returnToMenu();
            }
        }
    }

    // ========== UPDATE METHODS ==========

    public void update() {
        if (gameState != GameState.PLAYING) {
            return;
        }

        // Update all balls
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);

            if (!ball.isActive()) {
                ball.activate();
            }

            ball.update();
        }

        if (allBricksDestroyed()) {
            completeLevel();
        }
    }

    public void checkAllBallCollisions(double screenWidth, double screenHeight) {
        for (Ball ball : balls) {
            checkBallCollisions(ball, screenWidth, screenHeight);
        }
    }

    public void checkBallCollisions(Ball ball, double screenWidth, double screenHeight) {
        if (gameState != GameState.PLAYING || !ball.isActive()) {
            return;
        }

        ball.handleWallCollision(screenWidth, screenHeight);

        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && checkCollision(ball, brick)) {
                brick.hit();
                ball.handleBrickCollision(brick);
                score += 10;
                break;
            }
        }
    }

    public void checkAllBallsFall(Paddle paddle) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            if (ball.getY() > screenHeight) {
                if (balls.size() == 1) {
                    handleBallFall(ball, paddle);
                } else {
                    balls.remove(i);
                }
            }
        }
    }

    // ========== RENDER METHODS ==========

    public void render(GraphicsContext gc, Ball mainBall) {
        // Render bricks
        for (Brick brick : bricks) {
            brick.render(gc);
        }

        // Render all balls
        for (Ball ball : balls) {
            ball.render(gc);
        }

        // Render paddle
        if (paddle != null) {
            paddle.render(gc);
        }

        // Render game info
        renderGameInfo(gc, mainBall);
    }

    public void renderGameInfo(GraphicsContext gc, Ball ball) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));
        gc.fillText(String.format("Score: %d", score), 10, 20);
        gc.fillText(String.format("Lives: %d/%d", lives, maxLives), 10, 40);
        gc.fillText(String.format("Bricks: %d", getRemainingBricks()), 10, 60);

        if (gameState == GameState.WAITING_FOR_START && !ball.isActive()) {
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font("Arial", 18));
            gc.fillText("Press SPACE to start!", screenWidth / 2 - 100, screenHeight / 2 + 100);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("ESC: Menu | R: Reset | SPACE: Start", 10, screenHeight - 20);
    }

    public void renderMenu(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 48));
        gc.fillText("ARKANOID", screenWidth / 2 - 120, screenHeight / 2 - 100);

        gc.setFill(Color.GREEN);
        gc.fillRect(screenWidth / 2 - 80, screenHeight / 2 - 30, 160, 50);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("START", screenWidth / 2 - 30, screenHeight / 2);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Click START or press SPACE to play", screenWidth / 2 - 150, screenHeight / 2 + 50);
        gc.fillText("In game: SPACE to start ball", screenWidth / 2 - 130, screenHeight / 2 + 80);
    }

    public void renderGameOver(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        gc.setFill(Color.DARKRED);
        gc.fillRoundRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300, 20, 20);

        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        gc.strokeRoundRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300, 20, 20);

        gc.setFill(Color.RED);
        gc.setFont(new Font("Arial", 48));
        gc.fillText("GAME OVER", screenWidth / 2 - 120, screenHeight / 2 - 80);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 24));
        gc.fillText("Final Score: " + score, screenWidth / 2 - 80, screenHeight / 2 - 20);
        gc.fillText("Lives: " + lives + "/" + maxLives, screenWidth / 2 - 60, screenHeight / 2 + 10);

        gc.setFill(Color.GREEN);
        gc.fillRoundRect(screenWidth / 2 - 80, screenHeight / 2 + 40, 160, 40, 10, 10);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 18));
        gc.fillText("RESTART", screenWidth / 2 - 40, screenHeight / 2 + 65);

        gc.setFill(Color.BLUE);
        gc.fillRoundRect(screenWidth / 2 - 80, screenHeight / 2 + 90, 160, 40, 10, 10);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 18));
        gc.fillText("MENU", screenWidth / 2 - 30, screenHeight / 2 + 115);

        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Press R to restart or ESC for menu", screenWidth / 2 - 150, screenHeight / 2 + 160);
    }

    public void renderLevelComplete(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        gc.setFill(Color.DARKGREEN);
        gc.fillRoundRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300, 20, 20);

        gc.setStroke(Color.GREEN);
        gc.setLineWidth(3);
        gc.strokeRoundRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300, 20, 20);

        gc.setFill(Color.GREENYELLOW);
        gc.setFont(new Font("Arial", 36));
        gc.fillText("LEVEL COMPLETE!", screenWidth / 2 - 150, screenHeight / 2 - 80);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 24));
        gc.fillText("Score: " + score, screenWidth / 2 - 50, screenHeight / 2 - 20);
        gc.fillText("Lives: " + lives, screenWidth / 2 - 40, screenHeight / 2 + 20);

        gc.setFill(Color.ORANGE);
        gc.fillRoundRect(screenWidth / 2 - 80, screenHeight / 2 + 40, 160, 40, 10, 10);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 18));
        gc.fillText("CONTINUE", screenWidth / 2 - 45, screenHeight / 2 + 65);

        gc.setFill(Color.BLUE);
        gc.fillRoundRect(screenWidth / 2 - 80, screenHeight / 2 + 90, 160, 40, 10, 10);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 18));
        gc.fillText("MENU", screenWidth / 2 - 30, screenHeight / 2 + 115);

        gc.setFill(Color.YELLOW);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Press R to continue or ESC for menu", screenWidth / 2 - 160, screenHeight / 2 + 160);
    }

    // ========== BRICK MANAGEMENT ==========

    public void createBricks() {
        bricks.clear();

        int rows = 5;
        int cols = 10;
        double startX = 50;
        double startY = 50;
        double brickWidth = (screenWidth - 100 - (cols - 1) * 3) / cols;
        double brickHeight = 25;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 3);
                double y = startY + row * (brickHeight + 3);
                Brick brick = new Brick(x, y, brickWidth, brickHeight);
                bricks.add(brick);
            }
        }
    }

    private boolean checkCollision(GameObject obj1, GameObject obj2) {
        double margin = 0.5;
        return obj1.getX() + margin < obj2.getX() + obj2.getWidth() - margin &&
                obj1.getX() + obj1.getWidth() - margin > obj2.getX() + margin &&
                obj1.getY() + margin < obj2.getY() + obj2.getHeight() - margin &&
                obj1.getY() + obj1.getHeight() - margin > obj2.getY() + margin;
    }

    public boolean allBricksDestroyed() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    // ========== BUTTON CLICK DETECTION ==========

    public boolean isStartButtonClicked(double mouseX, double mouseY) {
        double buttonX = screenWidth / 2 - 80;
        double buttonY = screenHeight / 2 - 30;
        double buttonWidth = 160;
        double buttonHeight = 50;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    public boolean isRestartButtonClicked(double mouseX, double mouseY) {
        double buttonX = screenWidth / 2 - 80;
        double buttonY = screenHeight / 2 + 40;
        double buttonWidth = 160;
        double buttonHeight = 40;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    public boolean isMenuButtonClicked(double mouseX, double mouseY) {
        double buttonX = screenWidth / 2 - 80;
        double buttonY = screenHeight / 2 + 90;
        double buttonWidth = 160;
        double buttonHeight = 40;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    // ========== GETTER METHODS ==========

    public List<Brick> getBricks() { return new ArrayList<>(bricks); }
    public List<Ball> getBalls() { return balls; }
    public Paddle getPaddle() { return paddle; }
    public int getRemainingBricks() {
        return (int) bricks.stream().filter(brick -> !brick.isDestroyed()).count();
    }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getMaxLives() { return maxLives; }
    public boolean isGameOver() { return gameOver; }
    public GameState getGameState() { return gameState; }
    public boolean isInMenu() { return gameState == GameState.MENU; }
    public boolean isPlaying() { return gameState == GameState.PLAYING; }
    public boolean isGameOverState() { return gameState == GameState.GAME_OVER; }
    public boolean isLevelComplete() { return gameState == GameState.LEVEL_COMPLETE; }
    public boolean isWaitingForStart() { return gameState == GameState.WAITING_FOR_START; }
}
