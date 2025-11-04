package org.example.akarnoidgame;

import javafx.scene.Scene;
import javafx.stage.Stage;

// Lớp quản lý màn chơi, có thể mở rộng thêm menu, restart.
public class GameManager {
    private final Stage stage;
    private Scene gameScene;
    private GameCanvas gameCanvas;

    public GameManager(Stage stage) {
        this.stage = stage;
        initGame();
    }

    private void initGame() {
        gameCanvas = new GameCanvas(900, 600);
        gameScene = new Scene(gameCanvas);
        stage.setTitle("Arkanoid ");
        stage.setScene(gameScene);
        stage.show();
        gameCanvas.requestFocus();
    }
}
