package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Khởi tạo 1 quả bóng trong game.
 */
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

    /**
     * Thả bóng ra để nó bắt đầu bay.
     * @param initialDx v ban đầu theo hướng ngang
     * @param initialDy v ban đầu theo hướng dọc
     */
    public void releaseBall(double initialDx, double initialDy) {
        if (isStuck) {
            isStuck = false;
            dx = initialDx;
            dy = initialDy;
        }
    }

    /**
     * Tăng tốc độ di chuyển của bóng theo hệ số nhất định.
     * @param factor hệ số nhân tốc độ
     */
    public void increaseSpeed(double factor) {
        double maxSpeed = 15.0;
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);

        if (currentSpeed * factor < maxSpeed) {
            dx *= factor;
            dy *= factor;
        }
    }

    /**
     * Cập nhật trạng thái và vị trí của bóng trong mỗi frame.
     */
    @Override
    public void update() {
        if (isStuck) return;
        move();
        // Xử lý lỗi khi bóng kẹt tường
        if (x <= 0) {
            x = 0;
            GameMusic.getInstance().playPaddleHitSound();
            dx = -dx;
        }
        if (x + width >= canvasWidth) {
            x = canvasWidth - width;
            GameMusic.getInstance().playPaddleHitSound();
            dx = -dx;
        }
        if (y <= 0) {
            y = 0;
            GameMusic.getInstance().playPaddleHitSound();
            dy = -dy;
        }
    }

    /**
     * Vẽ bóng lên màn hình.
     * @param gc đối tượng GraphicsContext dùng để vẽ lên canvas
     */
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



