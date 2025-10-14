package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Ball extends MovableObject {
    private double baseSpeed;
    private boolean isActive = false;
    private double originalBaseSpeed;
    private Image ballImage;

    public Ball(double x, double y, double size, String imagePath) {
        super(x, y, size, size, imagePath);
        this.baseSpeed = 4.0;
        this.originalBaseSpeed = 4.0;
        this.dx = 0;
        this.dy = 0;
        loadBallImage();
    }

    public Ball(double x, double y, double size) {
        super(x, y, size, size, null);
        this.baseSpeed = 4.0;
        this.originalBaseSpeed = 4.0;
        this.dx = 0;
        this.dy = 0;
        loadBallImage();
    }

    private void loadBallImage() {
        try {
            ballImage = new Image(getClass().getResourceAsStream("/images/ball.png"));
        } catch (Exception e) {
            System.out.println("Could not load ball image: " + e.getMessage());
            ballImage = null;
        }
    }

    @Override
    public void update() {
        if (isActive) {
            move();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (ballImage != null) {
            gc.drawImage(ballImage, x, y, width, height);
        } else if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, width, height);
        }
    }

    public void activate() {
        if (!isActive) {
            isActive = true;
            baseSpeed = originalBaseSpeed;
            dx = baseSpeed * (Math.random() > 0.5 ? 1 : -1);
            dy = -baseSpeed;
        }
    }

    public void deactivate() {
        isActive = false;
        dx = 0;
        dy = 0;
        baseSpeed = originalBaseSpeed;
    }

    public void completeReset() {
        isActive = false;
        dx = 0;
        dy = 0;
        baseSpeed = originalBaseSpeed;
    }

    public void resetPosition(double paddleX, double paddleY, double paddleWidth) {
        double ballX = paddleX + paddleWidth / 2 - width / 2;
        double ballY = paddleY - height;
        setPosition(ballX, ballY);
    }

    public void bounceFromPaddle(double hitPosition) {
        if (!isActive) return;
        double angleMultiplier = (hitPosition < 0) ? 2.0 : 1.5 ;
        double angle = hitPosition * angleMultiplier;

        dx = baseSpeed * angle;
        dy = -baseSpeed;

        // Normalize speed
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        double targetSpeed = baseSpeed;
        if (currentSpeed > 0) {
            double ratio = targetSpeed / currentSpeed;
            dx *= ratio;
            dy *= ratio;
        }
    }

    public void handleWallCollision(double screenWidth, double screenHeight) {
        if (!isActive) return;

        if (x <= 0) {
            x = 1;
            dx = Math.abs(dx);
        } else if (x + width >= screenWidth) {
            x = screenWidth - width - 1;
            dx = -Math.abs(dx);
        }

        if (y <= 0) {
            y = 1;
            dy = Math.abs(dy);
        }
    }

    public void handleBrickCollision(Brick brick) {
        if (!isActive) return;

        double ballCenterX = x + width / 2;
        double ballCenterY = y + height / 2;
        double brickCenterX = brick.getX() + brick.getWidth() / 2;
        double brickCenterY = brick.getY() + brick.getHeight() / 2;

        double dxCenter = ballCenterX - brickCenterX;
        double dyCenter = ballCenterY - brickCenterY;

        double combinedWidth = (width + brick.getWidth()) / 2;
        double combinedHeight = (height + brick.getHeight()) / 2;

        double overlapX = combinedWidth - Math.abs(dxCenter);
        double overlapY = combinedHeight - Math.abs(dyCenter);

        if (overlapX < overlapY) {
            dx = -dx;
            if (dxCenter > 0) {
                x = brick.getX() + brick.getWidth() + 1;
            } else {
                x = brick.getX() - width - 1;
            }
        } else {
            dy = -dy;
            if (dyCenter > 0) {
                y = brick.getY() + brick.getHeight() + 1;
            } else {
                y = brick.getY() - height - 1;
            }
        }
    }

    public boolean collidesWith(Paddle paddle) {
        return x < paddle.getX() + paddle.getWidth() &&
                x + width > paddle.getX() &&
                y < paddle.getY() + paddle.getHeight() &&
                y + height > paddle.getY();
    }

    // ========== GETTERS & SETTERS ==========

    public boolean isActive() {
        return isActive;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public double getOriginalBaseSpeed() {
        return originalBaseSpeed;
    }

    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

