package game;

import java.awt.*;

public class Item {
    private int x, y;
    private int width, height;
    private String type; // ví dụ: "x2", "minus", "expand", "shrink"

    public Item(int x, int y, int width, int height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public void move() {
        // TODO: item rơi xuống
    }

    public void draw(Graphics g) {
        // TODO: vẽ item
    }

    public String getType() {
        return type;
    }
}