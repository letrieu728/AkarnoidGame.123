package org.example.akarnoidgamefx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private double baseWidth;
    private double screenWidth;

    public Paddle(double x, double y, double width, double height, double screenWidth) {
        super(x, y, width, height, null);
        this.baseWidth = width;
        this.screenWidth = screenWidth;
        this.dx = 0;
        this.dy = 0;
    }

    public Paddle(double x, double y, double width, double height, double screenWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.baseWidth = width;
        this.screenWidth = screenWidth;
        this.dx = 0;
        this.dy = 0;
    }

    @Override
    public void update() {
        // - Paddle đứng yên tại vị trí khởi tạo
    }

    @Override
    public void render(GraphicsContext gc) {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.WHITE);
            gc.strokeRect(x, y, width, height);
        }
    }
    public void moveLeft() {
        // Không làm gì - paddle không di chuyển
    }

    public void moveRight() {
        // Không làm gì - paddle không di chuyển
    }

    public void stop() {
        // Không làm gì - paddle không di chuyển
    }
    public void expand() {
        // Không làm gì - không có hiệu ứng
    }

    public void shrink() {
        // Không làm gì - không có hiệu ứng
    }

    public void updateItemEffects() {
        // Không làm gì - không có hiệu ứng
    }

    // Getter methods
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getBaseWidth() { return baseWidth; }

    public double getHitPosition(double ballX) {
        return (ballX - x) / width;
    }
}
