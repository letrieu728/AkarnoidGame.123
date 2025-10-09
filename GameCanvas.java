package game;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class GameCanvas extends Canvas {
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    public GameCanvas(double width, double height) {
        super(width, height);

        paddle = new Paddle(210, 420);
        ball = new Ball(240, 360);
        bricks = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                bricks.add(new Brick(60 + i * 60, 50 + j * 30));
            }
        }

        render();
    }

    public void render() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        paddle.render(gc);
        ball.render(gc);
        for (Brick b : bricks) b.render(gc);
    }
}
