package org.example.akarnoidgame;

// Lớp trừu tượng cho các đối tượng có thể di chuyển.
public abstract class MovableObject extends GameObject {
    protected double dx, dy;

    public MovableObject(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
    }

    public void move() {
        x += dx;
        y += dy;
    }
}

