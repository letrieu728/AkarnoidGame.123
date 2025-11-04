package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Paddle extends MovableObject {
    private final double speed = 7;
    private final double canvasWidth;

    // CÁC BIẾN MỚI ĐỂ XỬ LÝ EXPAND 
    private final double originalWidth;
    private boolean isExpanded = false;
    private long expansionEndTime;
    private static final double EXPANSION_AMOUNT = 15;

    public Paddle(double x, double y, double width, double height, double canvasWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.canvasWidth = canvasWidth;
        this.originalWidth = width;
    }
    public void expand() {
        if (!isExpanded) {
            this.x -= EXPANSION_AMOUNT;
            this.width += (EXPANSION_AMOUNT * 2);
            this.isExpanded = true;
        }
        this.expansionEndTime = System.currentTimeMillis() + 7000;
    }
    private void resetSize() {
        this.x += EXPANSION_AMOUNT;
        this.width = this.originalWidth;
        this.isExpanded = false;
    }

    @Override
    public void update() {
        // Kiểm tra nếu hết thời gian mở rộng 
        if (isExpanded && System.currentTimeMillis() > expansionEndTime) {
            resetSize();
        }
        // Va chạm biên
        move();
        if (x < 0) x = 0;
        if (x + width > canvasWidth) x = canvasWidth - width;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.DEEPSKYBLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    public void handleKeyPress(KeyCode code) {
        if (code == KeyCode.LEFT) dx = -speed;
        else if (code == KeyCode.RIGHT) dx = speed;
    }

    public void handleKeyRelease(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.RIGHT) dx = 0;
    }
}
