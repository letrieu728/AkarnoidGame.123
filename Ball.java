package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Ball extends MovableObject {
    private final double canvasWidth, canvasHeight;

    public Ball(double x, double y, double size, double canvasWidth, double canvasHeight, String imagePath) {
        super(x, y, size, size, imagePath);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        dx = 4;
        dy = -4;
    }

    @Override
    public void update() {
        move();

        // Bật tường trái/phải
        if (x <= 0 || x + width >= canvasWidth) dx = -dx;

        // Bật trần
        if (y <= 0) dy = -dy;

        // Nếu rơi xuống đáy
        if (y + height > canvasHeight) {
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

    public void bounce() {
        dy = -dy;
    }
}
