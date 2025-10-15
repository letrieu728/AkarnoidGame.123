package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private final double speed = 7;
    private final double canvasWidth;

    public Paddle(double x, double y, double width, double height, double canvasWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.canvasWidth = canvasWidth;
    }

    @Override
    public void update() {
        move();
        if (x < 0) x = 0;
        if (x + width > canvasWidth) x = canvasWidth - width;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.DEEPSKYBLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    public void handleKeyPress(KeyCode code) {
        if (code == KeyCode.LEFT) dx = -speed;
        else if (code == KeyCode.RIGHT) dx = speed;
    }

    public void handleKeyRelease(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.RIGHT) dx = 0;
    }
}
