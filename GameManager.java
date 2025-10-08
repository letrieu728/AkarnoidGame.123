package org.example.akarnoidgamefx;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Brick> bricks;
    private double screenWidth;
    private double screenHeight;

    public GameManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
        createBricks();
    }

    public void createBricks() {
        bricks.clear();

        int rows = 3;
        int cols = 10;
        double startX = 100;
        double startY = 50;
        double endX = 600;
        double endY = 300;

        double totalWidth = endX - startX;
        double totalHeight = endY - startY;

        double brickWidth = (totalWidth - (cols - 1) * 5) / cols;
        double brickHeight = (totalHeight - (rows - 1) * 3) / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 5);
                double y = startY + row * (brickHeight + 3);

                Brick brick = new Brick(x, y, brickWidth, brickHeight);
                bricks.add(brick);
            }
        }
    }

    public void update() {
        // Không có chuyển động
    }

    public void checkBallCollisions(Ball ball) {
        // Không xử lý va chạm
    }

    // Getter methods
    public List<Brick> getBricks() { 
        return new ArrayList<>(bricks); 
    }
    
    public double getScreenWidth() {
        return screenWidth;
    }
    
    public double getScreenHeight() {
        return screenHeight;
    }
}
