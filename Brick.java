package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Brick extends GameObject {

    private int hitPoints;         
    private boolean indestructible;
    private String type;            

    public Brick(double x, double y, double width, double height, String type) {
        super(x, y, width, height, null);
        this.type = type.toLowerCase();

        switch (this.type) {
            case "hard" -> {
                hitPoints = 3;
                indestructible = false;
                img = new Image(getClass().getResource("/image/brick1.png").toExternalForm());
            }
            case "indestructible" -> {
                hitPoints = Integer.MAX_VALUE;
                indestructible = true;
                img = new Image(getClass().getResource("/image/brick2.png").toExternalForm());
            }
            default -> { // "normal"
                hitPoints = 1;
                indestructible = false;
                img = new Image(getClass().getResource("/image/brick.png").toExternalForm());
            }
        }
    }

    public boolean hit() {
        if (indestructible) return false;

        hitPoints--;
        if (hitPoints <= 0) {
            visible = false; 
            return true;
        }
        return false;
    }

    public boolean isIndestructible() {
        return indestructible;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public String getType() {
        return type;
    }

    @Override
    public void update() {
        // Gạch không di chuyển
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!visible) return;

        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            
            switch (type) {
                case "hard" -> gc.setFill(Color.DARKRED);
                case "indestructible" -> gc.setFill(Color.DARKGRAY);
                default -> gc.setFill(Color.ORANGE);
            }
            gc.fillRect(x, y, width, height);
        }
    }
}
