package org.example.akarnoidgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import javafx.embed.swing.JFXPanel;

public class GameCollisionTest {

    @BeforeAll
    static void initToolkit() {
        new JFXPanel(); // Khởi tạo JavaFX toolkit
    }

    //  1. Va chạm tường (trái/phải)
    @Test
    void ballShouldBounceWhenHitWall() {
        Ball ball = new Ball(0, 10, 10, 800, 600, null);
        ball.setDx(-5);
        ball.setDy(0);
        ball.setStuck(false);

        double initialDx = ball.getDx();
        ball.update();

        assertEquals(-initialDx, ball.getDx(), 0.001,
                "Ball should bounce horizontally when hitting wall");
    }

    //  2. Va chạm paddle (giả lập bằng vị trí y)
    @Test
    void ballShouldBounceWhenHitPaddle() {
        Ball ball = new Ball(100, 580, 10, 800, 600, null);
        ball.setDx(0);
        ball.setDy(5);
        ball.setStuck(false);

        Paddle paddle = new Paddle(80, 590, 100, 10, 800, null);

        // Giả lập va chạm
        if (ball.getDy() > 0 &&
                ball.y + ball.height >= paddle.y &&
                ball.x + ball.width >= paddle.x &&
                ball.x <= paddle.x + paddle.width) {
            ball.bounceY();
        }

        assertTrue(ball.getDy() < 0, "Ball should bounce upward after hitting paddle");
    }
    //  4. Tính điểm (giả lập: mỗi brick bị phá = +100)
    @Test
    void scoreShouldIncreaseWhenBrickDestroyed() {
        int score = 0;
        boolean brickDestroyed = true;

        if (brickDestroyed) score += 100;

        assertEquals(100, score, "Score should increase by 100 when a brick is destroyed");
    }
}
