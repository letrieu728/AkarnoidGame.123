package org.example.akarnoidgame;

/**
 * Vật phẩm "đạn" - cho phép thanh đỡ bắn đạn để phá gạch.
 */
public class BulletPowerUp extends PowerUp {

    public BulletPowerUp(double x, double y) {
        super(x, y, 40, "/image/bullet.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.shootBullets();
    }

}
