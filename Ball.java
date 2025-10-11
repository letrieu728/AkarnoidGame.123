package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends MovableObject {
    
    public Ball(double x, double y, double size, String imagePath) {
        super(x, y, size, size, imagePath);
    }
    
    public Ball(double x, double y, double size) {
        super(x, y, size, size, null);
    }
    
    @Override
    public void update() {
        // Không update - không di chuyển
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, width, height);
        }
    }
    public double getSize() { return width; }
    public double getBaseSpeed() { return baseSpeed; }
    public void setBaseSpeed(double baseSpeed) { this.baseSpeed = baseSpeed; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
}


