package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;

public class Bullet extends MovableObject {

    public Bullet(double startX, double startY) {
        super(startX - 5, startY, 10, 20, "/image/bullet.png");
        this.dy = -8.0;
        this.dx = 0;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (visible && img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
}



