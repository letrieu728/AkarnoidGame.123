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

    /**
     * HÀM MỚI: Tăng tốc độ của bóng theo một hệ số.
     * @param factor Hệ số nhân (ví dụ: 1.02 để tăng 2%).
     */
    public void increaseSpeed(double factor) {
        // Giới hạn tốc độ tối đa để tránh bóng đi quá nhanh
        double maxSpeed = 15.0;
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);

        // Chỉ tăng tốc nếu chưa đạt tốc độ tối đa
        if (currentSpeed * factor < maxSpeed) {
            dx *= factor;
            dy *= factor;
        }
    }

    @Override
    public void update() {
        if (isStuck) return;

        move();

        if (x <= 0 || x + width >= canvasWidth) dx = -dx;
        if (y <= 0) dy = -dy;
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

    public void setDx(double dx) {
        this.dx = dx;
    }
    public double getDy(){
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
}
