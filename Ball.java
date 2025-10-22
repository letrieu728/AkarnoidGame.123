package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    private final double canvasWidth, canvasHeight;
    private boolean isStuck = true;

    public Ball(double x, double y, double size, double canvasWidth, double canvasHeight, String imagePath) {
        super(x, y, size, size, imagePath);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        dx = 0;
        dy = 0;
    }

    public void releaseBall(double initialDx, double initialDy) {
        if (isStuck) {
            isStuck = false;
            dx = initialDx;
            dy = initialDy;
        }
    }

    public void increaseSpeed(double factor) {
        double maxSpeed = 15.0;
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);

        if (currentSpeed * factor < maxSpeed) {
            dx *= factor;
            dy *= factor;
        }
    }

    @Override
    public void update() {
        if (isStuck) return;

        move();

        // ✨ --- SỬA LỖI KẸT BÓNG Ở TƯỜNG --- ✨
        // Xử lý va chạm tường trái
        if (x <= 0) {
            x = 0; // Đặt lại vị trí bóng ngay tại biên
            GameMusic.getInstance().playPaddleHitSound();
            dx = -dx; // Đảo ngược hướng
        }
        // Xử lý va chạm tường phải
        if (x + width >= canvasWidth) {
            x = canvasWidth - width; // Đặt lại vị trí bóng ngay tại biên
            GameMusic.getInstance().playPaddleHitSound();
            dx = -dx; // Đảo ngược hướng
        }

        // Xử lý va chạm trần
        if (y <= 0) {
            y = 0; // Đặt lại vị trí bóng ngay tại biên (tùy chọn, nhưng nên có)
            GameMusic.getInstance().playPaddleHitSound();
            dy = -dy;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, width, height);
        }
    }

    public void bounceY() {
        dy = -dy;
    }

    public void bounceX() {
        dx = -dx;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public void setStuck(boolean stuck) {
        isStuck = stuck;
    }

    public double getDx() {
        return dx;
    }

    public double getDy(){
        return dy;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}

