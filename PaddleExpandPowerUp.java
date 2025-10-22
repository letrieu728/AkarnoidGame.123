package org.example.akarnoidgame;

public class PaddleExpandPowerUp extends PowerUp {

    public PaddleExpandPowerUp(double x, double y) {
        super(x, y, 40, "/image/expand.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.getPaddle().expand();
    }
}