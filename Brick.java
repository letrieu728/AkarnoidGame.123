package game;

import javafx.scene.canvas.GraphicsContext;

public class Brick extends GameObject {
    public Brick(double x, double y) {
        super(x, y, 50, 25, "/images/brick.png");
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y, width, height);
    }

    @Override
    public void update() { }
}
