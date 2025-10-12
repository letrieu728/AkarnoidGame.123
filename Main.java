package org.example.akarnoidgamefx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        GameCanvas gameCanvas = new GameCanvas(WIDTH, HEIGHT);
        Scene scene = new Scene(new javafx.scene.Group(gameCanvas));

        stage.setTitle("Arkanoid - Group 4");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Đặt focus cho canvas để nhận input
        gameCanvas.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
