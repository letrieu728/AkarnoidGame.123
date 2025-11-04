package org.example.akarnoidgame;

public class BulletPowerUp extends PowerUp {

    public BulletPowerUp(double x, double y) {
        super(x, y, 40, "/image/bullet.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.shootBullets();
    }

}
