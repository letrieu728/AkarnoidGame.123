package org.example.akarnoidgame;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

import java.awt.GraphicsEnvironment;
public class GameMusic {

    private static GameMusic instance;

    public static GameMusic getInstance() {
        if (instance == null) {
            instance = new GameMusic();
        }
        return instance;
    }
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer brickBreakPlayer;
    private MediaPlayer paddleHitPlayer;
    private MediaPlayer loseLifePlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer youWinPlayer;
    private MediaPlayer buttonClickPlayer;
    private MediaPlayer powerUpPlayer;

    /**
     * Private constructor để ngăn việc tạo đối tượng từ bên ngoài.
     */
    private GameMusic() {
        ensureJavaFXInitialized();
        initializeSounds();
    }
    private void ensureJavaFXInitialized() {
        try {
            new JFXPanel(); // Khởi động toolkit nếu chưa khởi tạo
        } catch (Exception ignored) {
        }
    }

    /**
     * Khởi tạo toàn bộ âm thanh cần thiết cho game.
     */
    private void initializeSounds() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[GameMusic] Headless environment detected — skipping audio initialization.");
            return;
        }

        try {
            // nạp nhạc nền
            backgroundMusicPlayer = loadSound("background.mp3");
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundMusicPlayer.setVolume(0.3);
            }
            // nạp các hiệu ứng âm thanh khác
            brickBreakPlayer = loadSound("brick_break.mp3");
            paddleHitPlayer = loadSound("paddle_hit.mp3");
            loseLifePlayer = loadSound("lose_life.mp3");
            gameOverPlayer = loadSound("game_over.mp3");
            youWinPlayer = loadSound("you_win.mp3");
            buttonClickPlayer = loadSound("button_click.mp3");
            powerUpPlayer = loadSound("powerup.mp3");

        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private MediaPlayer loadSound(String fileName) {
        try {
            URL resource = getClass().getResource("/sounds/" + fileName);
            if (resource == null) {
                throw new IllegalArgumentException("Không tìm thấy file âm thanh: " + fileName);
            }
            Media media = new Media(resource.toString());
            return new MediaPlayer(media);
        } catch (Exception e) {
            System.err.println("Không thể tải file âm thanh: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
    public void playBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }
    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.pause();
        }
    }

    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play();
        }
    }
    // stop() rồi play() để âm thanh có thể phát lại ngay lập tức nếu được gọi liên tiếp.
    private void playSoundEffect(MediaPlayer player) {
        if (player != null) {
            player.stop();
            player.play();
        }
    }

    public void playBrickBreakSound() {
        playSoundEffect(brickBreakPlayer);
    }

    public void playPaddleHitSound() {
        playSoundEffect(paddleHitPlayer);
    }

    public void playLoseLifeSound() {
        playSoundEffect(loseLifePlayer);
    }

    public void playGameOverSound() {
        playSoundEffect(gameOverPlayer);
    }

    public void playYouWinSound() {
        playSoundEffect(youWinPlayer);
    }

    public void playButtonClickSound() {
        playSoundEffect(buttonClickPlayer);
    }

    public void playPowerUpSound() {
        playSoundEffect(powerUpPlayer);
    }
}





