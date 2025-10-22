package org.example.akarnoidgame;

public class TruDiemPowerUp extends PowerUp {

    public TruDiemPowerUp(double x, double y) {
        super(x, y, 40, "/image/trudiem.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.addScore(-50);
    }
}
