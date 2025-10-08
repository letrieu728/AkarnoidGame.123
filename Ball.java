package org.example.akarnoidgamefx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    private double baseSpeed;
    private long speedEffectEndTime = 0;

    public Ball(double x, double y, double size, String imagePath) {
        super(x, y, size, size, imagePath);
        this.baseSpeed = 3.0;
        this.dx = baseSpeed;
        this.dy = -baseSpeed;
    }

    public Ball(double x, double y, double size) {
        super(x, y, size, size, null);
        this.baseSpeed = 3.0;
        this.dx = baseSpeed;
        this.dy = -baseSpeed;
    }
    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }
    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        move();
        updateItemEffects();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, width, height);
            gc.setStroke(Color.WHITE);
            gc.strokeOval(x, y, width, height);
        }
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    public void bounceFromPaddle(double hitPosition) {
        double angle = (hitPosition - 0.5) * 2.0;
        dx = baseSpeed * angle * 2.0;
        dy = -Math.abs(dy);

        if (Math.abs(dx) < 2.0) {
            dx = dx < 0 ? -2.0 : 2.0;
        }
    }

    public void increaseSpeed() {
        dx *= 1.5;
        dy *= 1.5;
        speedEffectEndTime = System.currentTimeMillis() + 10000;
    }

    public void decreaseSpeed() {
        dx *= 0.7;
        dy *= 0.7;
        speedEffectEndTime = System.currentTimeMillis() + 10000;
    }

    public void resetSpeed() {
        double directionX = dx < 0 ? -1.0 : 1.0;
        double directionY = dy < 0 ? -1.0 : 1.0;
        dx = baseSpeed * directionX;
        dy = baseSpeed * directionY;
    }

    public void updateItemEffects() {
        if (speedEffectEndTime > 0 && System.currentTimeMillis() > speedEffectEndTime) {
            resetSpeed();
            speedEffectEndTime = 0;
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getSize() { return width; }
    public double getBaseSpeed() { return baseSpeed; }
    public void setBaseSpeed(double baseSpeed) { this.baseSpeed = baseSpeed; }
    public boolean collidesWith(Paddle paddle) {
        return x < paddle.getX() + paddle.getWidth() &&
                x + width > paddle.getX() &&
                y < paddle.getY() + paddle.getHeight() &&
                y + height > paddle.getY();
    }
    public boolean collidesWith(Brick brick) {
        return x < brick.getX() + brick.getWidth() &&
                x + width > brick.getX() &&
                y < brick.getY() + brick.getHeight() &&
                y + height > brick.getY();
    }

}