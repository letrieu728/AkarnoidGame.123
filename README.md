# AkarnoidGame.123

## 📌 Giới thiệu
Đây là dự án game **Arkanoid** (game đập gạch) viết bằng **Java Swing** theo hướng đối tượng (OOP).  
Trò chơi được mở rộng so với bản gốc với nhiều **tính năng sáng tạo** do nhóm xây dựng.  

---

## 👨‍👩‍👧‍👦 Thành viên nhóm & phụ trách
- **Lê Quốc Triệu (24022750)** – Game Panel + Bullet
- **Lê Tuấn Dũng  (24022630)** – Phát triển Ball +  va chạm
- **Nguyễn Thạc Quang Huy  (24022662)** – Xây dựng Paddle + Input
- **Trương Thị Kim Ánh  (24022614)** – Xây dựng brick  + item

---


---

## 🚀 Cách chạy
1. Mở project trong **IntelliJ IDEA** hoặc IDE Java bất kỳ.  
2. Đảm bảo đã cài **JDK 17+**.  
3. Đặt thư mục `resources` thành **Resources Root** trong IntelliJ.  
4. Chạy `Main.java`.  

---

## 🎮 Gameplay
Người chơi điều khiển paddle để đỡ bóng và phá vỡ gạch.  
Trong quá trình chơi có nhiều **hiệu ứng đặc biệt**:  

### 🧩 Item (Power-ups / Power-downs)
- **⭐ Nhân đôi điểm (x2 Score)** → khi hứng vào, toàn bộ điểm số hiện tại được nhân đôi.  
- **❌ Trừ điểm (- Score)** → bị trừ một lượng điểm cố định.  
- **⚪ Nhân đôi bóng (Double Ball)** → mỗi bóng hiện tại sinh ra thêm một bóng mới.  
- **🔫 Súng (Laser)** → paddle có thể bắn đạn trong **5 giây**.  

### 🔫 Bullet
- Khi paddle đang trong trạng thái **Laser**, người chơi có thể nhấn **Space** để bắn đạn.  
- Đạn bay thẳng lên, nếu trúng gạch → gạch sẽ bị phá hủy.  

---

## 🛠 Công nghệ sử dụng
- **Java SE**  
- **Java Swing (JPanel, JFrame)** để vẽ và render game.  
- **ImageIO** để load ảnh PNG từ thư mục `resources/assets/`.  
- **OOP** (tách class riêng cho Ball, Paddle, Brick, Item, Bullet).  

---

## 🌟 Điểm sáng tạo
- Hệ thống **Item đa dạng** (x2 điểm, trừ điểm, nhân đôi bóng, laser).  
- Paddle có thể **bắn đạn trong thời gian hữu hạn**.  
- Dễ mở rộng thêm hiệu ứng hoặc chế độ chơi khác.  

---
