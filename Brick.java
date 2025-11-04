package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Brick extends GameObject {

    private int hitPoints;
    private final boolean indestructible;
    private final String type;

    /**
     * Constructor cho đối tượng Brick.
     * @param x tọa độ X của gạch
     * @param y tọa độ Y của gạch
     * @param width chiều rộng của gạch
     * @param height chiều cao của gạch
     * @param type loại gạch
     */
    public Brick(double x, double y, double width, double height, String type) {
        super(x, y, width, height, null);
        this.type = type.toLowerCase();

        // Định nghĩa thuộc tính cho từng loại gạch
        switch (this.type) {
            case "brick1":
                hitPoints = 2;
                indestructible = false;
                img = loadImage("/image/brick1.png");
                break;
            case "brick2":
                hitPoints = 4;
                indestructible = false;
                img = loadImage("/image/brick2.png");
                break;
            default:
                hitPoints = 1;
                indestructible = false;
                img = loadImage("/image/brick.png");
                break;
        }
    }

    public boolean hit() {
        if (indestructible) {
            return false;
        }
        hitPoints--;
        if (hitPoints <= 0) {
            visible = false;
            return true;
        }
        return false;
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh: " + path);
            return null;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            switch (type) {
                case "brick1" -> gc.setFill(Color.YELLOW);
                case "indestructible" -> gc.setFill(Color.DARKGRAY);
                default -> gc.setFill(Color.ORANGE);
            }
            gc.fillRect(x, y, width, height);
        }
    }

    @Override
    public void update() { /* Gạch không di chuyển */ }
    public boolean isIndestructible() { return indestructible; }
    public int getHitPoints() { return hitPoints; }
    public String getType() { return type; }
}


