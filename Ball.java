package game;

import java.awt.*;

public class Ball {
    private int x, y;      // vị trí
    private int dx, dy;    // vận tốc
    private int radius;    // bán kính

    public Ball(int x, int y, int dx, int dy, int radius) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
    }

    public void move() {
        // TODO: cập nhật vị trí bóng
    }

    public void draw(Graphics g) {
        // TODO: vẽ bóng
    }

    // getter, setter nếu cần
}