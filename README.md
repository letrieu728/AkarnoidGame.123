# 🎮 Arkanoid – Phiên bản JavaFX

## 👨‍👩‍👧‍👦 Thành viên nhóm & phụ trách
- **Lê Quốc Triệu (24022750)** – GameObject + Paddle
- **Lê Tuấn Dũng  (24022630)** – Ball + Brick
- **Nguyễn Thạc Quang Huy  (24022662)** – Items + Main
- **Trương Thị Kim Ánh  (24022614)** – GameManager + GameCanvas

## 🧠 Mục tiêu
Xây dựng game Arkanoid (Đập gạch) bằng JavaFX với mô hình hướng đối tượng (OOP), thể hiện đóng gói, kế thừa và hiển thị đồ họa cơ bản.

---

## ⚙️ Công nghệ sử dụng
- **Ngôn ngữ:** Java 25
- **Thư viện GUI:** JavaFX
- **IDE:** IntelliJ IDEA
- **Ảnh game:** PNG nền trong suốt (paddle, ball, brick)

---

## 📁 Cấu trúc thư mục hiện tại
```
ArkanoidGame/
 ├── src/
 │    └── game/
 │         ├── Main.java
 │         ├── GameCanvas.java
 │         ├── GameObject.java
 │         ├── MovableObject.java
 │         ├── Paddle.java
 │         ├── Ball.java
 │         └── Brick.java
 └── resources/
      └── images/
           ├── paddle.png
           ├── ball.png
           └── brick.png
```

---

## 💡 Mô tả các lớp chính
| Class | Vai trò | Kế thừa từ | Ghi chú |
|-------|----------|------------|----------|
| `GameObject` | Lớp cha trừu tượng cho tất cả vật thể trong game | — | Quản lý toạ độ, kích thước, ảnh |
| `MovableObject` | Lớp cha cho các vật thể di chuyển được | `GameObject` | Thêm vận tốc `dx`, `dy` |
| `Ball` | Quả bóng di chuyển, va chạm | `MovableObject` | Có ảnh `ball.png` |
| `Paddle` | Thanh trượt điều khiển | `GameObject` | Có ảnh `paddle.png` |
| `Brick` | Gạch tĩnh để phá | `GameObject` | Có ảnh `brick.png` |
| `GameCanvas` | Canvas chính để vẽ toàn cảnh | — | Render toàn bộ vật thể |
| `Main` | Điểm bắt đầu chương trình | — | Khởi tạo JavaFX Stage và Scene |

---

## 🎨 Hiển thị hiện tại (Tuần 5)
- Render **nền đen**
- 3 hàng **gạch cam**
- **Paddle xanh** ở dưới
- **Bóng đỏ** ở giữa

---

## ▶️ Cách chạy chương trình
1. Vào IntelliJ → **Run → Edit Configurations...**
2. Thêm dòng sau vào ô **VM Options** (thay đường dẫn bằng SDK JavaFX của bạn):
   ```
   --module-path "C:\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics
   ```
3. Chạy file `Main.java`
4. Cửa sổ “Arkanoid - JavaFX Render with Images” xuất hiện 🎮

---



## 🏁 Kế hoạch phát triển tuần sau
- 🕹️ Thêm di chuyển Paddle bằng phím ← →
- 🧱 Thêm va chạm bóng–gạch
- 🌟 Thêm Item rơi (x2 điểm, bắn đạn, nhân đôi bóng)
