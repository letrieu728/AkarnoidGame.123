package org.example.akarnoidgame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {

    private final List<Integer> powerUpHighScores = new ArrayList<>();
    private final List<Integer> speedRunHighScores = new ArrayList<>();
    private static final String POWER_UP_SCORE_FILE = "highscore_powerup.txt";
    private static final String SPEED_RUN_SCORE_FILE = "highscore_speedrun.txt";
    private static final int MAX_HIGH_SCORES = 5; // Chỉ lưu top 5

    public HighScoreManager() {
        // Tự động tải điểm khi khởi tạo
        loadAllHighScores();
    }

    public void loadAllHighScores() {
        loadScoresFromFile(POWER_UP_SCORE_FILE, powerUpHighScores);
        loadScoresFromFile(SPEED_RUN_SCORE_FILE, speedRunHighScores);
    }

    private void loadScoresFromFile(String fileName, List<Integer> scoreList) {
        scoreList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scoreList.add(Integer.parseInt(line));
                } catch (NumberFormatException e) {
                    System.err.println("Bỏ qua dòng điểm không hợp lệ: " + line);
                }
            }
            scoreList.sort(Collections.reverseOrder());
        } catch (FileNotFoundException e) {
            System.out.println("Không tìm thấy file: " + fileName + ". Sẽ tạo file mới.");
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + fileName);
            e.printStackTrace();
        }
    }

    private void saveScoresToFile(String fileName, List<Integer> scoreList) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int s : scoreList) {
                writer.println(s);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file: " + fileName);
        }
    }

    public void checkAndAddHighScore(int newScore, GameCanvas.GameMode mode) {
        List<Integer> targetList = (mode == GameCanvas.GameMode.POWER_UP) ? powerUpHighScores : speedRunHighScores;
        String targetFile = (mode == GameCanvas.GameMode.POWER_UP) ? POWER_UP_SCORE_FILE : SPEED_RUN_SCORE_FILE;

        targetList.add(newScore);
        targetList.sort(Collections.reverseOrder());

        while (targetList.size() > MAX_HIGH_SCORES) {
            targetList.remove(targetList.size() - 1);
        }

        saveScoresToFile(targetFile, targetList);
    }

    // Getters để GameCanvas có thể truy cập và hiển thị
    public List<Integer> getPowerUpHighScores() {
        return powerUpHighScores;
    }

    public List<Integer> getSpeedRunHighScores() {
        return speedRunHighScores;
    }
}