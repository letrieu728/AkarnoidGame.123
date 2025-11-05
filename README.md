# 🎮 Arkanoid – Phiên bản JavaFX

👨‍👩‍👧‍👦 Thành viên nhóm & Phụ trách
1. Lê Tuấn Dũng

Tạo vòng lặp AnimationTimer và hàm update() tổng thể

Xử lý trạng thái game: MENU, PLAYING, PAUSED, GAMEOVER, YOUWIN

Viết các hàm: handleBrickCollision(), handleBallLost(), upadte(), render(), animationTimer(), xử lý sự kiện bàn phím/chuột

Quản lý timeline, chuyển màn, level, hiệu ứng thắng/thua

Điều phối toàn bộ render

Xử lý âm thanh (GameMusic.getInstance()...) và luồng nhạc

Liên quan đến: toàn bộ hệ thống game (class GameCanvas.java)

2. Lê Quốc Triệu

Vẽ UML

Cập nhật chuyển động và va chạm của Ball, Paddle, Brick, Bullet trong update()

Gọi paddle.update(), ball.update() và phần render tương ứng

Xử lý danh sách balls, bricks, bullets

Liên kết hàm vật lý trong handleBrickCollision()

Liên quan đến: Ball.java, Paddle.java, Brick.java, Bullet.java, MovableObject.java, README.md

3. Trương Thị Kim Ánh

Xử lý phần sinh và cập nhật Power-Up: spawnPowerUp(), updatePowerUpsAndBullets()

Gọi p.applyEffect(this) khi va chạm với paddle

Tích hợp logic nhân đôi bóng, mở rộng paddle, trừ điểm, v.v.

Liên quan đến: PowerUp.java, X2BallPowerUp.java, X2ScorePowerUp.java, PaddleExpandPowerUp.java, BulletPowerUp.java, TruDiemPowerUp.java, JUnit

4. Nguyễn Thạc Quang Huy

Thiết kế và viết phần giao diện: renderMenu(), renderPauseScreen(), renderGameOver(), renderYouWin(), renderHighScores()

Lưu và đọc điểm cao: loadAllHighScores(), saveScoresToFile(), checkAndAddHighScore()

Giao diện chọn chế độ chơi và bảng xếp hạng

Liên quan đến: GameMusic.java, file điểm highscore_powerup.txt, highscore_speedrun.txt
## 🧠 Mục tiêu
Xây dựng game Arkanoid (Đập gạch) bằng JavaFX với mô hình hướng đối tượng (OOP), thể hiện đóng gói, kế thừa và hiển thị đồ họa cơ bản.

---

## ⚙️ Công nghệ sử dụng
- **Ngôn ngữ:** Java 21
- **Thư viện GUI:** JavaFX
- **IDE:** IntelliJ IDEA
- **Ảnh game:** PNG nền trong suốt (paddle, ball, brick)

---

## 📁 Cấu trúc thư mục 
```
ArkanoidGame/
│
├── 📄 pom.xml                 # (nếu dùng Maven)
├── 📄 ArkanoidGame.iml        # File cấu hình IntelliJ
│
├── 📂 src/
│   └── 📂 main/
│       ├── 📂 java/
│       │   └── 📂 org/example/akarnoidgame/
│       │       ├── 📄 Main.java
│       │       ├── 📄 GameCanvas.java          # Trung tâm logic & render
│       │       ├── 📄 GameMusic.java           # Quản lý âm thanh
│       │       ├── 📄 GameObject.java          # Lớp cha cơ bản
│       │       ├── 📄 MovableObject.java       # Lớp cha cho vật thể di chuyển
│       │       ├── 📄 Ball.java                # Bóng
│       │       ├── 📄 Paddle.java              # Thanh đỡ
│       │       ├── 📄 Brick.java               # Gạch
│       │       ├── 📄 Bullet.java              # Đạn (khi có PowerUp)
│       │       ├── 📄 PowerUp.java             # Lớp cha cho vật phẩm
│       │       ├── 📄 X2BallPowerUp.java       # PowerUp: nhân đôi bóng
│       │       ├── 📄 X2ScorePowerUp.java      # PowerUp: nhân đôi điểm
│       │       ├── 📄 PaddleExpandPowerUp.java # PowerUp: mở rộng Paddle
│       │       ├── 📄 BulletPowerUp.java       # PowerUp: bắn đạn
│       │       └── 📄 TruDiemPowerUp.java      # PowerUp: trừ điểm
│       │
│       └── 📂 resources/
│           ├── 📂 images/
│           │   ├── background.png
│           │   ├── ball.png
│           │   ├── brick.png
│           │   ├── brick1.png
│           │   ├── brick2.png
│           │   ├── bullet.png
│           │   ├── expand.png
│           │   ├── heart.png
│           │   ├── laser.png
│           │   ├── paddle.png
│           │   ├── trudiem.png
│           │   ├── x2ball.png
│           │   └── x2score.png
│           │
│           └── 📂 sounds/
│               ├── background.mp3
│               ├── brick_break.mp3
│               ├── button_click.mp3
│               ├── game_over.mp3
│               ├── lose_life.mp3
│               ├── paddle_hit.mp3
│               ├── powerup.mp3
│               └── you_win.mp3
│
├── 📄 highscore_powerup.txt    # Lưu điểm cao chế độ Power-Up
├── 📄 highscore_speedrun.txt   # Lưu điểm cao chế độ Speed-Run
└── 📄 README.md                # Tài liệu mô tả dự án, hướng dẫn chơi

---

📘 Mô tả các lớp chính

GameCanvas.java – Lớp trung tâm điều khiển game: cập nhật logic, xử lý va chạm, vẽ khung hình, quản lý trạng thái (Menu, Pause, Win, Game Over) và nhập từ bàn phím/chuột.

GameObject.java – Lớp cha của tất cả đối tượng trong game, chứa vị trí, kích thước, hình ảnh và hàm kiểm tra va chạm.

MovableObject.java – Kế thừa GameObject, thêm vận tốc và khả năng di chuyển, bật lại khi va chạm.

Ball.java – Quả bóng, di chuyển, bật lại khi chạm tường/paddle, phá gạch, hỗ trợ nhân đôi bóng.

Paddle.java – Thanh đỡ người chơi điều khiển, di chuyển ngang và phản xạ bóng.

Brick.java – Gạch có thể phá, cộng điểm khi vỡ, có thể rơi vật phẩm.

Bullet.java – Đạn bắn ra từ paddle khi có power-up laser, phá gạch khi va chạm.

PowerUp.java – Lớp cha cho vật phẩm tăng cường, rơi xuống và kích hoạt hiệu ứng khi chạm paddle.

X2BallPowerUp.java – Nhân đôi số bóng.

PaddleExpandPowerUp.java – Mở rộng kích thước paddle.

BulletPowerUp.java – Cho phép paddle bắn đạn.

X2ScorePowerUp.java – Nhân đôi điểm.

TruDiemPowerUp.java – Trừ điểm người chơi.

GameMusic.java – Quản lý toàn bộ âm thanh (nhạc nền, va chạm, thắng, thua, power-up).

highscore_powerup.txt / highscore_speedrun.txt – Lưu điểm cao nhất của từng chế độ chơi.

---
## 🧩 Sơ đồ UML tổng quan
 ```
![alt](https://github.com/letrieu728/AkarnoidGame.123/blob/main/resources/image/uml.png?raw=true)

 ```

## ▶️ Cách chạy chương trình
1. Vào IntelliJ → **Run → Edit Configurations...**
2. Thêm dòng sau vào ô **VM Options** (thay đường dẫn bằng SDK JavaFX của bạn):
   ```
   --module-path "C:\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics,javafx.swing  --enable-native-access=javafx.graphics 
   ```
3. Chạy file `Main.java`
4. Cửa sổ “Arkanoid - JavaFX Render with Images” xuất hiện 🎮

---


