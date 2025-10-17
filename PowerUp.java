package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;

public class PowerUp extends MovableObject {

    // Enum để định nghĩa các loại power-up
    public enum PowerUpType {
        BULLET, LASER, TRUDIEM, X2BALL, X2SCORE, EXPAND // <-- THÊM MỚI
    }

    private final PowerUpType type;

    public PowerUp(double x, double y, double size, PowerUpType type) {
        super(x, y, size, size, getImagePathForType(type));
        this.type = type;

        // Thiết lập tốc độ rơi xuống
        this.dy = 2.0;
        this.dx = 0;
    }

    // Lấy đường dẫn ảnh dựa vào loại power-up
    private static String getImagePathForType(PowerUpType type) {
        // Tên file ảnh được quy ước là "tênloại.png" (viết thường)
        // Ví dụ: expand.png
        return "/image/" + type.name().toLowerCase() + ".png";
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

    public PowerUpType getType() {
        return type;
    }
}
