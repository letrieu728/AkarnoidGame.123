package game;

import java.awt.*;

public class Bullet {
    private int x, y;
    private int width, height;
    private int speed;
    private boolean active;

    public Bullet(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.active = true;
    }

    public void move() {
        // TODO: di chuyển đạn bay lên
        y -= speed;
        if (y < 0) {
            active = false; // ra ngoài màn hình thì hủy
        }
    }

    public void draw(Graphics g) {
        if (active) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // getter & setter nếu cần
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
