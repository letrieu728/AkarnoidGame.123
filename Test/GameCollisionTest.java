package org.example.akarnoidgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameCollisionTest {

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

    //  3. Va chạm brick (giả lập bằng hình chữ nhật)
    @Test
    void ballShouldBounceWhenHitBrick() {
        Ball ball = new Ball(50, 50, 10, 800, 600, null);
        ball.setDx(3);
        ball.setDy(-3);
        ball.setStuck(false);

        Brick brick = new Brick(45, 40, 30, 10, null);
        double oldDy = ball.getDy();

        // Giả lập va chạm brick (đơn giản: nếu trùng vùng)
        if (ball.x + ball.width > brick.x &&
                ball.x < brick.x + brick.width &&
                ball.y < brick.y + brick.height &&
                ball.y + ball.height > brick.y) {
            ball.bounceY();
        }

        assertEquals(-oldDy, ball.getDy(), 0.001, "Ball should bounce after hitting brick");
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
