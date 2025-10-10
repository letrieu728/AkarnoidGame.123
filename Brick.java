package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GameObject {
    private int health;
    private boolean destroyed = false;

    public Brick(double x, double y, double width, double height) {
        super(x, y, width, height, null);
        this.health = 1;
    }

    public Brick(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
        this.health = 1;
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;

        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            switch (health) {
                case 3 -> gc.setFill(Color.DARKRED);
                case 2 -> gc.setFill(Color.ORANGE);
                default -> gc.setFill(Color.YELLOW);
            }
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    public void hit() {
        if (destroyed) return;
        health--;
        if (health <= 0) {
            destroyed = true;
            visible = false;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int h) {
        this.health = h;
    }
}
