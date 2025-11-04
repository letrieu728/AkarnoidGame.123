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
    private static final double EXPANSION_AMOUNT = 15; // Lượng mở rộng mỗi bên

    public Paddle(double x, double y, double width, double height, double canvasWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.canvasWidth = canvasWidth;
        this.originalWidth = width; // Lưu lại chiều rộng ban đầu
    }

    // HÀM MỚI: Kích hoạt hiệu ứng mở rộng
    public void expand() {
        // Nếu paddle chưa được mở rộng, hãy mở rộng nó
        if (!isExpanded) {
            this.x -= EXPANSION_AMOUNT; // Dịch sang trái 15px
            this.width += (EXPANSION_AMOUNT * 2); // Tăng chiều rộng 30px
            this.isExpanded = true;
        }
        // Đặt lại thời gian hiệu lực (kể cả khi đang được mở rộng)
        this.expansionEndTime = System.currentTimeMillis() + 7000; // 7 giây từ bây giờ
    }

    // HÀM MỚI: Quay về kích thước ban đầu 
    private void resetSize() {
        this.x += EXPANSION_AMOUNT; // Dịch lại sang phải 15px
        this.width = this.originalWidth; // Trả về chiều rộng ban đầu
        this.isExpanded = false;
    }

    @Override
    public void update() {
        // LOGIC MỚI: Kiểm tra nếu hết thời gian mở rộng 
        if (isExpanded && System.currentTimeMillis() > expansionEndTime) {
            resetSize();
        }

        // Logic di chuyển và kiểm tra biên không đổi
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
