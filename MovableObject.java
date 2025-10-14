package game;

/**
 * Lớp trung gian cho các đối tượng có thể di chuyển
 *
 * Kế thừa từ GameObject và bổ sung thuộc tính vận tốc (dx, dy),
 * giúp các lớp con dễ dàng điều khiển chuyển động.
 */
public abstract class MovableObject extends GameObject {
    // Thành phần vận tốc theo trục X và Y
    protected double dx;
    protected double dy;

    /**
     * Khởi tạo đối tượng di chuyển.
     */
    public MovableObject(double x, double y, double width, double height, String imagePath) {
        super(x, y, width, height, imagePath);
        this.dx = 0;
        this.dy = 0;
    }

    /**
     * Phương thức di chuyển cơ bản:
     * cộng vận tốc vào vị trí hiện tại.
     */
    public void move() {
        this.x += dx;
        this.y += dy;
    }

    // ======== Getter & Setter ========

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Đặt vận tốc đồng thời trên 2 trục.
     */
    public void setVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Kiểm tra xem đối tượng có đang đứng yên không.
     * @return true nếu dx = dy = 0
     */
    public boolean isStationary() {
        return dx == 0 && dy == 0;
    }

    /**
     * Dừng chuyển động ngay lập tức.
     */
    public void stopMovement() {
        this.dx = 0;
        this.dy = 0;
    }
}

