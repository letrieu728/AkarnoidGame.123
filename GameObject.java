package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Lớp cơ sở trừu tượng cho tất cả đối tượng trong game.
public abstract class GameObject {
    protected double x, y, width, height;
    protected boolean visible = true;
    protected Image img;

    /**
     * Khởi tạo một đối tượng game(GameObject)
     * @param x tọa độ X ban đầu của đối tượng
     * @param y tọa độ Y ban đầu của đối tượng
     * @param width chiều rộng của đối tượng
     * @param height chiều cao của đối tượng
     * @param imagePath đường dẫn
     */
    public GameObject(double x, double y, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                img = new Image(getClass().getResource(imagePath).toExternalForm());
            } catch (NullPointerException e) {
                System.err.println("Không thể tìm thấy tài nguyên hình ảnh tại: " + imagePath);
                img = null;
            }
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
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}


