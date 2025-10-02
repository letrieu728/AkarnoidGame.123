package game;

import java.awt.*;

public class Brick {
    private int x, y;
    private int width, height;
    private boolean destroyed;

    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
    }

    public void draw(Graphics g) {
        if (!destroyed) {
            // TODO: vẽ gạch
        }
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}