package org.example.akarnoidgame;

public class X2ScorePowerUp extends PowerUp {

    public X2ScorePowerUp(double x, double y) {
        super(x, y, 40, "/image/x2score.png");
    }

    @Override
    public void applyEffect(GameCanvas gameCanvas) {
        gameCanvas.multiplyScore(2);
    }
}
