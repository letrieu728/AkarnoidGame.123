package org.example.akarnoidgame;
import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp trừu tượng cho tất cả các vật phẩm Power-up.
 */
public abstract class PowerUp extends MovableObject {

    public PowerUp(double x, double y, double size, String imagePath) {
        super(x, y, size, size, imagePath);
        this.dy = 2.0;
        this.dx = 0;
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (visible && img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
    public abstract void applyEffect(GameCanvas gameCanvas);
}


