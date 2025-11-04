package org.example.akarnoidgame;

/**
 * Vật phẩm "mở rộng thanh đỡ" - giúp người chơi dễ bắt bóng hơn.
 */
public class PaddleExpandPowerUp extends PowerUp {

    public PaddleExpandPowerUp(double x, double y) {
        super(x, y, 40, "/image/expand.png");
    }
    
    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.getPaddle().expand();
    }
}
