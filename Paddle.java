package org.example.akarnoidgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * Đại diện cho thanh paddle trong game.
 * Paddle có thể di chuyển trái/phải và tạm thời mở rộng kích thước.
 */
public class Paddle extends MovableObject {
    private final double speed = 7;
    private final double canvasWidth;
    private final double originalWidth;
    private boolean isExpanded = false;
    private long expansionEndTime;
    private static final double EXPANSION_AMOUNT = 15;

    /**
     * Tạo paddle mới.
     *
     * @param x           tọa độ X
     * @param y           tọa độ Y
     * @param width       chiều rộng
     * @param height      chiều cao
     * @param canvasWidth chiều rộng canvas
     * @param imagePath   đường dẫn ảnh paddle
     */
    public Paddle(double x, double y, double width, double height, double canvasWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.canvasWidth = canvasWidth;
        this.originalWidth = width;
    }

    /** Mở rộng paddle trong 5 giây. */
    public void expand() {
        if (!isExpanded) {
            x -= EXPANSION_AMOUNT;
            width += EXPANSION_AMOUNT * 2;
            isExpanded = true;
        }
        expansionEndTime = System.currentTimeMillis() + 5000;
    }

    /** Trả paddle về kích thước ban đầu. */
    private void resetSize() {
        x += EXPANSION_AMOUNT;
        width = originalWidth;
        isExpanded = false;
    }

    /** Cập nhật vị trí và trạng thái mở rộng. */
    @Override
    public void update() {
        if (isExpanded && System.currentTimeMillis() > expansionEndTime)
            resetSize();

        move();
        if (x < 0) x = 0;
        if (x + width > canvasWidth) x = canvasWidth - width;
    }

    /** Vẽ paddle. */
    @Override
    public void render(GraphicsContext gc) {
        if (img != null)
            gc.drawImage(img, x, y, width, height);
        else {
            gc.setFill(Color.DEEPSKYBLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    /** Xử lý phím nhấn trái/phải. */
    public void handleKeyPress(KeyCode code) {
        if (code == KeyCode.LEFT) dx = -speed;
        else if (code == KeyCode.RIGHT) dx = speed;
    }

    /** Xử lý khi thả phím. */
    public void handleKeyRelease(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.RIGHT) dx = 0;
    }
}

