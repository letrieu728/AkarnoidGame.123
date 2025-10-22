package org.example.akarnoidgame;

public class BulletPowerUp extends PowerUp {

    public BulletPowerUp(double x, double y) {
        // Gọi constructor của lớp cha với đường dẫn ảnh tương ứng
        super(x, y, 40, "/image/bullet.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        // Hiệu ứng của vật phẩm này là gọi hàm bắn đạn trong GameCanvas
        gameCanvas.shootBullets();
    }
}