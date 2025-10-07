package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected double x, y;
    protected double width, height;
    protected Image img;

    public GameObject(double x, double y, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = new Image(getClass().getResourceAsStream(imagePath));
    }

    public abstract void render(GraphicsContext gc);
    public abstract void update();
}

