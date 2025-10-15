package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public abstract class GameObject {
    protected double x, y, width, height;
    protected boolean visible = true;
    protected Image img;

    public GameObject(double x, double y, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (imagePath != null) {
            img = new Image(imagePath);
        }
    }

    public abstract void update();
    public abstract void render(GraphicsContext gc);

    public boolean intersects(GameObject other) {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }

    // Getter & Setter
    public double getX() { return x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
