package game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Brick> bricks;
    private Paddle paddle;
    private Ball ball;
    private double screenWidth;
    private double screenHeight;
    
    public GameManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.bricks = new ArrayList<>();
        createGameObjects();
    }
    
    private void createGameObjects() {
        createBricks();
        createPaddle();
        createBall();
    }
    
    private void createBricks() {
        bricks.clear();
        
        int rows = 3;
        int cols = 10;
        double startX = 100;
        double startY = 50;
        double brickWidth = 50;
        double brickHeight = 20;
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = startX + col * (brickWidth + 5);
                double y = startY + row * (brickHeight + 3);
                Brick brick = new Brick(x, y, brickWidth, brickHeight);
                bricks.add(brick);
            }
        }
    }
    private void createPaddle() {
        double paddleWidth = 100;
        double paddleHeight = 15;
        double paddleX = screenWidth / 2 - paddleWidth / 2;
        double paddleY = screenHeight - 50;
        
        this.paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);
    }

    private void createBall() {
        double ballSize = 15;
        double ballX = paddle.getX() + paddle.getWidth() / 2 - ballSize / 2;
        double ballY = paddle.getY() - ballSize;
        
        this.ball = new Ball(ballX, ballY, ballSize);
    }

    public List<Brick> getBricks() { 
        return new ArrayList<>(bricks); 
    }
    
    public Paddle getPaddle() {
        return paddle;
    }
    
    public Ball getBall() {
        return ball;
    }
}
