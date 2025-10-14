package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Paddle extends MovableObject {
    private double baseWidth;
    private double moveSpeed = 8.0;
    private long sizeEffectEndTime = 0;
    private double screenWidth;
    private Image paddleImage; // Thêm biến để lưu ảnh paddle

    public Paddle(double x, double y, double width, double height, double screenWidth) {
        super(x, y, width, height, null);
        this.baseWidth = width;
        this.screenWidth = screenWidth;
        loadPaddleImage(); // Load ảnh khi khởi tạo
    }

    public Paddle(double x, double y, double width, double height, double screenWidth, String imagePath) {
        super(x, y, width, height, imagePath);
        this.baseWidth = width;
        this.screenWidth = screenWidth;
        loadPaddleImage(); // Load ảnh khi khởi tạo
    }

    // Phương thức load ảnh paddle
    private void loadPaddleImage() {
        try {
            // Thử load ảnh từ thư mục resources
            paddleImage = new Image(getClass().getResourceAsStream("/images/paddle.png"));
            System.out.println("Paddle image loaded successfully: " + (paddleImage != null));
        } catch (Exception e) {
            System.out.println("Could not load paddle image: " + e.getMessage());
            paddleImage = null;
        }
    }

    @Override
    public void update() {
        move();
        updateItemEffects();

        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (paddleImage != null) {
            gc.drawImage(paddleImage, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    // Các phương thức di chuyển
    public void moveLeft() {
        dx = -moveSpeed;
    }

    public void moveRight() {
        dx = moveSpeed;
    }

    public void stop() {
        dx = 0;
    }

    // Các phương thức power-up
    public void expand() {
        double oldX = x;
        width = baseWidth * 1.5;
        x = oldX - (width - baseWidth) / 2;
        sizeEffectEndTime = System.currentTimeMillis() + 15000;
    }

    public void shrink() {
        double oldX = x;
        width = Math.max(baseWidth / 2, 30);
        x = oldX + (baseWidth - width) / 2;
        sizeEffectEndTime = System.currentTimeMillis() + 10000;
    }

    public void updateItemEffects() {
        if (sizeEffectEndTime > 0 && System.currentTimeMillis() > sizeEffectEndTime) {
            double oldX = x;
            x = oldX + (width - baseWidth) / 2;
            width = baseWidth;
            sizeEffectEndTime = 0;
        }
    }

    // Getter methods
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getBaseWidth() { return baseWidth; }

    // Phương thức tính vị trí va chạm với ball
    public double getHitPosition(double ballCenterX) {
        double relativePosition = (ballCenterX - x) / width;
        relativePosition = Math.max(0, Math.min(1, relativePosition));
        double hitPosition = (relativePosition - 0.5) * 2;
        return hitPosition;
    }

    // Getter cho ảnh paddle (tuỳ chọn)
    public Image getPaddleImage() {
        return paddleImage;
    }

    // Setter để thay đổi ảnh paddle runtime (tuỳ chọn)
    public void setPaddleImage(String imagePath) {
        try {
            paddleImage = new Image(getClass().getResourceAsStream(imagePath));
            System.out.println("Paddle image changed to: " + imagePath);
        } catch (Exception e) {
            System.out.println("Could not set paddle image: " + e.getMessage());
            paddleImage = null;
        }
    }
}
