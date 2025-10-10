package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected double x, y;
    protected double width, height;
    protected Image img;
    protected boolean visible = true;

    public GameObject(double x, double y, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        if (imagePath != null) {
            try {
                img = new Image(getClass().getResourceAsStream(imagePath));
            } catch (Exception e) {
                System.out.println("Không thể tải ảnh: " + imagePath);
            }
        }
    }

    public abstract void render(GraphicsContext gc);
    public abstract void update();

    public boolean intersects(GameObject other) {
        return visible && other.visible &&
                x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void setVisible(boolean v) { this.visible = v; }
    public boolean isVisible() { return visible; }
}
