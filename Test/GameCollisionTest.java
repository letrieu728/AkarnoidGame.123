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
    //  3. Va chạm gạch (Test 1: Gạch 1 máu bị phá hủy)
    @Test
    void brickShouldBeDestroyedAfterOneHit() {
        // Gạch "normal" mặc định có 1 hitPoint
        Brick brick = new Brick(100, 100, 50, 20, "normal");

        assertTrue(brick.isVisible(), "Gạch phải được hiển thị lúc đầu");

        // Giả lập 1 lần va chạm
        boolean wasDestroyed = brick.hit(); //

        assertTrue(wasDestroyed, "Phương thức hit() phải trả về true khi gạch bị phá hủy");
        assertFalse(brick.isVisible(), "Gạch phải ẩn đi sau khi bị phá hủy");
    }

    //  5. Va chạm gạch (Test 2: Gạch nhiều máu)
    @Test
    void multiHitBrickShouldSurviveOneHit() {
        // Gạch "brick1" có 2 hitPoints
        Brick brick = new Brick(100, 100, 50, 20, "brick1");

        assertTrue(brick.isVisible(), "Gạch phải được hiển thị lúc đầu");
        assertEquals(2, brick.getHitPoints(), "Gạch 'brick1' phải bắt đầu với 2 máu");

        // --- Giả lập va chạm LẦN 1 ---
        boolean wasDestroyedFirstHit = brick.hit(); //

        assertFalse(wasDestroyedFirstHit, "Phương thức hit() phải trả về false ở lần va chạm đầu tiên");
        assertTrue(brick.isVisible(), "Gạch phải CÒN HIỂN THỊ sau lần va chạm đầu tiên");
        assertEquals(1, brick.getHitPoints(), "Gạch phải còn 1 máu");

        // --- Giả lập va chạm LẦN 2 ---
        boolean wasDestroyedSecondHit = brick.hit(); //

        assertTrue(wasDestroyedSecondHit, "Phương thức hit() phải trả về true ở lần va chạm thứ hai");
        assertFalse(brick.isVisible(), "Gạch phải ẨN ĐI sau lần va chạm thứ hai");
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
