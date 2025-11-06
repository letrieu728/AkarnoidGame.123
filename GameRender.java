package org.example.akarnoidgame;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class GameRenderer {
    private final GraphicsContext gc;
    private final Image background;

    public GameRenderer(GraphicsContext gc, Image background) {
        this.gc = gc;
        this.background = background;
    }

    /**
     * Vẽ toàn bộ khung hình của game tùy theo trạng thái hiên tại.
     * @param state trạng thái hiện tại
     * @param mode chế độ chơi hiện tại
     * @param paddle thanh chắn
     * @param bricks gạch
     * @param balls bóng
     * @param powerUps vật phẩm
     * @param bullets đạn
     * @param score điểm hiện tại
     * @param lives số mạng còn tại
     * @param level mức độ hiện tại
     * @param canvasWidth  chiều rộng vùng vẽ
     * @param canvasHeight chiều cao vùng vẽ
     * @param scoresToDisplay danh sách điểm cần hiển thị
     * @param highScoreTitle tiêu đề
     */
    public void render(GameCanvas.GameState state,
                       GameCanvas.GameMode mode,
                       Paddle paddle,
                       List<Brick> bricks,
                       List<Ball> balls,
                       List<PowerUp> powerUps,
                       List<Bullet> bullets,
                       int score,
                       int lives,
                       int level,
                       double canvasWidth,
                       double canvasHeight,
                       List<Integer> scoresToDisplay,
                       String highScoreTitle) {

        gc.clearRect(0, 0, canvasWidth, canvasHeight);
        gc.drawImage(background, 0, 0, canvasWidth, canvasHeight);

        switch (state) {
            case PLAYING -> {
                renderGameObjects(paddle, bricks, balls, powerUps, bullets, mode);
                renderUI(score, lives, level, canvasWidth);
            }
            case PAUSED -> {
                renderGameObjects(paddle, bricks, balls, powerUps, bullets, mode);
                renderUI(score, lives, level, canvasWidth);
                renderPauseScreen(canvasWidth, canvasHeight);
            }
            case MENU -> renderMenu(canvasWidth, canvasHeight);
            case HIGH_SCORE_SELECTION -> renderHighScoreSelection(canvasWidth, canvasHeight);
            case HIGH_SCORE -> renderHighScores(scoresToDisplay, highScoreTitle, canvasWidth, canvasHeight);
            case GAMEOVER -> renderGameOver(score, canvasWidth, canvasHeight);
            case YOUWIN -> renderYouWin(score, canvasWidth, canvasHeight);
        }
    }

    private void renderGameObjects(Paddle paddle, List<Brick> bricks, List<Ball> balls,
                                   List<PowerUp> powerUps, List<Bullet> bullets, GameCanvas.GameMode mode) {
        paddle.render(gc);
        for (Brick b : bricks) b.render(gc);
        for (Ball ball : balls) ball.render(gc);
        if (mode == GameCanvas.GameMode.POWER_UP) {
            for (PowerUp p : powerUps) p.render(gc);
            for (Bullet b : bullets) b.render(gc);
        }
    }

    private void renderUI(int score, int lives, int level, double canvasWidth) {
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 24));

        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Điểm: " + score, 10, 30);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("Mạng: " + lives, canvasWidth - 10, 30);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Level: " + level, canvasWidth / 2, 30);
    }

    private void renderPauseScreen(double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("PAUSED", width / 2, height / 2);

        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Nhấn 'P' để tiếp tục", width / 2, height / 2 + 50);
    }

    private void renderMenu(double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.CYAN);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("ARKANOID", width / 2, height / 3);

        drawButton(width, height / 2 - 40, 280, 60, Color.LIMEGREEN, "Chế độ Power-Up", Color.BLACK);
        drawButton(width, height / 2 + 40, 280, 60, Color.ORANGERED, "Chế độ Tốc độ", Color.WHITE);
        drawButton(width, height / 2 + 120, 280, 60, Color.GOLD, "Bảng Xếp Hạng", Color.BLACK);
        drawButton(width, height / 2 + 200, 280, 60, Color.DARKGRAY, "Thoát Game", Color.WHITE);
    }

    private void renderHighScores(List<Integer> scores, String title, double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 48));
        gc.fillText(title, width / 2, height / 4);

        gc.setFont(Font.font("Arial", 30));
        gc.setFill(Color.WHITE);
        double startY = height / 2 - 80;

        if (scores == null || scores.isEmpty()) {
            gc.fillText("Chưa có điểm nào", width / 2, startY);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                gc.fillText((i + 1) + ".   " + scores.get(i), width / 2, startY + i * 40);
            }
        }
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để quay lại", width / 2, height - 100);
    }

    private void renderHighScoreSelection(double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Arial", 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("CHỌN BẢNG XẾP HẠNG", width / 2, height / 3);
        gc.setFont(Font.font("Arial", 24));

        drawButton(width, height / 2 - 40, 280, 60, Color.LIMEGREEN, "BXH Power-Up", Color.BLACK);
        drawButton(width, height / 2 + 40, 280, 60, Color.ORANGERED, "BXH Tốc Độ", Color.WHITE);
        drawButton(width, height / 2 + 120, 280, 60, Color.DARKGRAY, "Quay lại", Color.WHITE);
    }

    private void renderGameOver(int score, double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("GAME OVER", width / 2, height / 2 - 40);
        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm của bạn: " + score, width / 2, height / 2 + 20);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", width / 2, height / 2 + 70);
    }

    private void renderYouWin(int score, double width, double height) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 60));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("YOU WIN!", width / 2, height / 2 - 40);
        gc.setFont(Font.font("Arial", 30));
        gc.fillText("Điểm cuối cùng: " + score, width / 2, height / 2 + 20);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText("Click để chơi lại", width / 2, height / 2 + 70);
    }

    private void drawButton(double width, double y, double w, double h, Color color, String text, Color textColor) {
        gc.setFill(color);
        gc.fillRoundRect(width / 2 - w / 2, y, w, h, 20, 20);
        gc.setFill(textColor);
        gc.setFont(Font.font("Arial", 24));
        gc.fillText(text, width / 2, y + 30);
    }
}
