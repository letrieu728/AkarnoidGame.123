package game;

import javafx.scene.canvas.GraphicsContext;

public abstract class MovableObject extends GameObject {
    protected double dx, dy;

    public MovableObject(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
        this.dx = 0;
        this.dy = 0;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void clamp(double screenWidth) {
        if (x < 0) x = 0;
        if (x + width > screenWidth) x = screenWidth - width;
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render(GraphicsContext gc);
}
