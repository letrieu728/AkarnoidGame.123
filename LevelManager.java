package org.example.akarnoidgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelManager {

    private final Random random = new Random();

    public List<Brick> loadLevel(int level, double canvasWidth) {
        switch (level) {
            case 2:
                return setupLayoutTriangle(canvasWidth);
            case 3:
                return setupLayoutCorners(canvasWidth);
            default: // Case 1
                return setupLayoutClassic(canvasWidth);
        }
    }

    private List<Brick> setupLayoutClassic(double width) {
        List<Brick> bricks = new ArrayList<>();
        int rows = 5;
        int cols = 10;
        double brickWidth = 60;
        double brickHeight = 25;
        double startX = 80;
        double startY = 80;
        double gapX = 20;
        double gapY = 15;
        String[] brickTypes = {"normal", "brick1", "brick2"};

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gapX);
                double y = startY + row * (brickHeight + gapY);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
        return bricks;
    }

    private List<Brick> setupLayoutTriangle(double width) {
        List<Brick> bricks = new ArrayList<>();
        int numRows = 7;
        double brickWidth = 60;
        double brickHeight = 25;
        double gapX = 10;
        double gapY = 10;
        String[] brickTypes = {"normal", "brick1", "brick2"};

        for (int row = 0; row < numRows; row++) {
            int numBricksInRow = row + 1;
            double totalRowWidth = numBricksInRow * brickWidth + (numBricksInRow - 1) * gapX;
            double startX = (width - totalRowWidth) / 2;
            double y = 80 + row * (brickHeight + gapY);
            for (int col = 0; col < numBricksInRow; col++) {
                double x = startX + col * (brickWidth + gapX);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
        return bricks;
    }

    private List<Brick> setupLayoutCorners(double width) {
        List<Brick> bricks = new ArrayList<>();
        int rowsPerCorner = 3;
        int colsPerCorner = 4;
        double brickWidth = 50;
        double brickHeight = 20;
        double gap = 10;

        createBrickRectangle(bricks, 60, 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(bricks, width - 60 - (colsPerCorner * (brickWidth + gap)), 80, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(bricks, 60, 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        createBrickRectangle(bricks, width - 60 - (colsPerCorner * (brickWidth + gap)), 300, rowsPerCorner, colsPerCorner, brickWidth, brickHeight, gap);
        return bricks;
    }

    private void createBrickRectangle(List<Brick> bricks, double startX, double startY, int rows, int cols, double brickWidth, double brickHeight, double gap) {
        String[] brickTypes = {"normal", "brick1", "brick2"};
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + gap);
                double y = startY + row * (brickHeight + gap);
                String randomType = brickTypes[random.nextInt(brickTypes.length)];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, randomType));
            }
        }
    }
}