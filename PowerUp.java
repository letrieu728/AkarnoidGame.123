package org.example.akarnoidgame;
import javafx.scene.canvas.GraphicsContext;
/**
 * LỚP TRỪU TƯỢNG MỚI cho tất cả các vật phẩm Power-up.
 * Kế thừa từ MovableObject để có thể di chuyển.
 */
public abstract class PowerUp extends MovableObject {

    public PowerUp(double x, double y, double size, String imagePath) {
        super(x, y, size, size, imagePath);
        // Tất cả power-up đều rơi xuống với tốc độ như nhau
        this.dy = 2.0;
        this.dx = 0;
    }

    @Override
    public void update() {
        move(); // Di chuyển vật phẩm xuống dưới
    }

    @Override
    public void render(GraphicsContext gc) {
        if (visible && img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
    public abstract void applyEffect(GameCanvas gameCanvas);
}
