package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Brick extends GameObject {

    private int hitPoints;
    private final boolean indestructible;
    private final String type;

    public Brick(double x, double y, double width, double height, String type) {
        super(x, y, width, height, null); // Image sẽ được load trong switch
        this.type = type.toLowerCase();

        // Định nghĩa thuộc tính cho từng loại gạch
        switch (this.type) {
            // ✨ --- SỬA LẠI TÊN CASE VÀ ĐƯỜNG DẪN ẢNH --- ✨
            case "brick1": // Tên mới: Gạch 2 lần chạm
                hitPoints = 2;
                indestructible = false;
                img = loadImage("/images/brick1.png"); // Ảnh cho gạch 2 máu
                break;
            case "brick2": // Tên mới: Gạch 4 lần chạm
                hitPoints = 4;
                indestructible = false;
                img = loadImage("/images/brick2.png"); // Ảnh cho gạch 4 máu
                break;
            default: // Gạch "normal" mặc định
                hitPoints = 1;
                indestructible = false;
                img = loadImage("/images/brick.png"); // Ảnh cho gạch thường
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
