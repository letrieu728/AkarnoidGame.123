package game;

import javafx.scene.canvas.GraphicsContext;

public class Paddle extends GameObject {
    public Paddle(double x, double y) {
        super(x, y, 80, 20, "/images/paddle.png");
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y, width, height);
    }

    @Override
    public void update() { }
}
