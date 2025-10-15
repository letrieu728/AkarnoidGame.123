package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Item extends GameObject {
    private double dy;
    private boolean active;
    private String type;

    public Item(double x, double y, double width, double height, String imagePath, String type) {
        super(x, y, width, height, imagePath);
        this.dy = 2;       // tốc độ rơi mặc định
        this.active = false;
        this.type = type;
    }

    @Override
    public void update() {
        y += dy;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillOval(x, y, width, height);
        }
    }

    // Khi item chạm paddle
    public void activate() {
        this.active = true;
        this.visible = false;
        // TODO: gọi hiệu ứng tương ứng tuỳ type (sẽ code sau)
        System.out.println("Item activated: " + type);
    }

    public boolean isActive() { return active; }
    public String getType() { return type; }
}
