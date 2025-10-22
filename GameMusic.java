package org.example.akarnoidgame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Lớp quản lý tất cả âm thanh trong game.
 * Sử dụng mẫu thiết kế Singleton để đảm bảo chỉ có một đối tượng GameMusic tồn tại.
 */
public class GameMusic {

    // --- Singleton Pattern ---
    private static final GameMusic instance = new GameMusic();

    public static GameMusic getInstance() {
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
        initializeSounds();
    }

    private void initializeSounds() {
        try {
            // Nhạc nền (lặp lại vô hạn)
            backgroundMusicPlayer = loadSound("nhạc nền.mp3");
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp lại
                backgroundMusicPlayer.setVolume(0.3); // Giảm âm lượng nhạc nền
            }

            // Âm thanh hiệu ứng
            brickBreakPlayer = loadSound("phá brick.mp3");
            paddleHitPlayer = loadSound("va chạm paddle-tường.mp3");
            loseLifePlayer = loadSound("mất 1 mạng.mp3");
            gameOverPlayer = loadSound("game over.mp3");
            youWinPlayer = loadSound("you win.mp3");
            buttonClickPlayer = loadSound("bấm nút.mp3");
            powerUpPlayer = loadSound("ăn được powerup.mp3");

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
