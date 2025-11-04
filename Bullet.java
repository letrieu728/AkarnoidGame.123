package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;

public class Bullet extends MovableObject {

    public Bullet(double startX, double startY) {
        // Khởi tạo viên đạn với kích thước 10x20 pixels
        // startX được truyền vào là tâm của paddle, ta trừ đi 5 để viên đạn nằm chính giữa
        super(startX - 5, startY, 10, 20, "/images/bullet.png");

        // Thiết lập tốc độ bay thẳng lên trên
        this.dy = -8.0; // Giá trị âm để di chuyển lên
        this.dx = 0;
    }

    @Override
    public void update() {
        move(); // Di chuyển viên đạn theo vận tốc dy
    }

    @Override
    public void render(GraphicsContext gc) {
        if (visible && img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
}
