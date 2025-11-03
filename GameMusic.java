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

    // --- Media Players ---
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer brickBreakPlayer;
    private MediaPlayer paddleHitPlayer;
    private MediaPlayer loseLifePlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer youWinPlayer;
    private MediaPlayer buttonClickPlayer;
    private MediaPlayer powerUpPlayer;

    private GameMusic() {
        // Private constructor để ngăn việc tạo đối tượng từ bên ngoài
        ensureJavaFXInitialized();
        initializeSounds();
    }
    private void ensureJavaFXInitialized() {
        try {
            new JFXPanel(); // Khởi động toolkit nếu chưa khởi tạo
        } catch (Exception ignored) {
        }
    }

    private void initializeSounds() {
        // Nếu đang chạy test (headless environment), bỏ qua load Media
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[GameMusic] Headless environment detected — skipping audio initialization.");
            return;
        }

        try {
            // Nhạc nền (lặp lại vô hạn)
            backgroundMusicPlayer = loadSound("background.mp3");
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp lại
                backgroundMusicPlayer.setVolume(0.3); // Giảm âm lượng nhạc nền
            }

            // Âm thanh hiệu ứng
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
            // Đường dẫn tới file âm thanh trong resources
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

    // --- Các phương thức công khai để điều khiển âm thanh ---

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

    // ✨ --- THÊM PHƯƠNG THỨC NÀY --- ✨
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.play(); // MediaPlayer.play() sẽ tự động tiếp tục nếu đang pause
        }
    }

    // Đối với các hiệu ứng âm thanh, chúng ta stop() rồi play()
    // để âm thanh có thể phát lại ngay lập tức nếu được gọi liên tiếp.
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
