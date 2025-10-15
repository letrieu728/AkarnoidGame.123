package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GameObject {
    public Brick(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
    }

    @Override
    public void update() {}

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.ORANGE);
            gc.fillRect(x, y, width, height);
        }
    }
}
