package org.example.akarnoidgame;

/**
 * Vật phẩm "x2 bóng" - khi nhặt được sẽ x2 bóng trong game.
 */
public class X2BallPowerUp extends PowerUp {

    public X2BallPowerUp(double x, double y) {
        super(x, y, 40, "/image/x2ball.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.multiplyBalls();
    }
}
