# 🔧 HƯỚNG DẪN SỬA DATABASE

## Vấn đề:
Database BTL.db hiện tại có thể thiếu bảng hoặc cấu trúc không đúng.

## Giải pháp: Tạo lại database đúng cấu trúc

### Cách 1: Sử dụng DB Browser for SQLite (KHUYẾN NGHỊ)

#### Bước 1: Tải DB Browser
1. Tải về từ: https://sqlitebrowser.org/dl/
2. Cài đặt DB Browser for SQLite

#### Bước 2: Tạo database mới
1. Mở DB Browser for SQLite
2. Chọn **"New Database"**
3. Lưu file với tên: **BTL.db**
4. Chọn vị trí: `app/src/main/assets/BTL.db` (ghi đè file cũ)

#### Bước 3: Tạo bảng nguoi_dung
1. Nhấn **"Create Table"**
2. Tên bảng: `nguoi_dung`
3. Thêm các cột:
   - `id` - Type: INTEGER - Primary Key - Autoincrement ✓
   - `ten_dang_nhap` - Type: TEXT - Not Null ✓ - Unique ✓
   - `email` - Type: TEXT - Not Null ✓
   - `mat_khau` - Type: TEXT - Not Null ✓
4. Nhấn **OK**

#### Bước 4: Tạo bảng cong_viec
1. Nhấn **"Create Table"**
2. Tên bảng: `cong_viec`
3. Thêm các cột:
   - `id` - Type: INTEGER - Primary Key - Autoincrement ✓
   - `tieu_de` - Type: TEXT - Not Null ✓
   - `mo_ta` - Type: TEXT
   - `ngay_bat_dau` - Type: TEXT
   - `han_hoan_thanh` - Type: TEXT
   - `trang_thai` - Type: TEXT
   - `muc_do_uu_tien` - Type: TEXT
   - `nguoi_dung_id` - Type: INTEGER
   - `loai_id` - Type: INTEGER
4. Nhấn **OK**

#### Bước 5: Thêm dữ liệu mẫu
1. Chọn tab **"Execute SQL"**
2. Copy và paste đoạn SQL sau:

```sql
-- Thêm tài khoản admin
INSERT INTO nguoi_dung (ten_dang_nhap, email, mat_khau) 
VALUES ('admin', 'admin@example.com', 'admin123');

-- Thêm công việc mẫu
INSERT INTO cong_viec (tieu_de, mo_ta, ngay_bat_dau, han_hoan_thanh, trang_thai, muc_do_uu_tien, nguoi_dung_id, loai_id)
VALUES 
('Hoàn thành báo cáo', 'Viết báo cáo tổng kết tháng', '2026-03-12', '2026-03-15', 'Đang làm', 'Cao', 1, 1),
('Họp team', 'Họp review sprint', '2026-03-13', '2026-03-13', 'Chưa bắt đầu', 'Trung bình', 1, 2),
('Học Kotlin', 'Hoàn thành khóa học Kotlin', '2026-03-10', '2026-03-20', 'Đang làm', 'Thấp', 1, 3);
```

3. Nhấn nút **"Execute"** (biểu tượng ▶)

#### Bước 6: Lưu database
1. Nhấn **"Write Changes"** (Ctrl + S)
2. Đóng DB Browser

#### Bước 7: Kiểm tra
1. Đảm bảo file `app/src/main/assets/BTL.db` đã được tạo/cập nhật
2. Chạy app trong Android Studio

---

### Cách 2: Sử dụng file SQL có sẵn

#### Nếu bạn có SQLite command line:

1. Mở Command Prompt/Terminal
2. Di chuyển đến thư mục project:
   ```
   cd C:\Users\truon\AndroidStudioProjects\BTL
   ```

3. Xóa database cũ:
   ```
   del app\src\main\assets\BTL.db
   ```

4. Tạo database mới từ file SQL:
   ```
   sqlite3 app\src\main\assets\BTL.db < create_database.sql
   ```

---

### Cách 3: Để app tự động tạo (ĐƠN GIẢN NHẤT)

Nếu bạn không muốn tạo database thủ công:

1. **Xóa file database cũ**:
   - Xóa file: `app/src/main/assets/BTL.db`

2. **Chạy app**:
   - App sẽ tự động tạo database mới với cấu trúc đúng
   - Tự động thêm tài khoản admin/admin123

3. **Đăng nhập**:
   - Username: admin
   - Password: admin123

---

## ✅ Cấu trúc database đúng:

### Bảng: nguoi_dung
```
id              INTEGER PRIMARY KEY AUTOINCREMENT
ten_dang_nhap   TEXT NOT NULL UNIQUE
email           TEXT NOT NULL
mat_khau        TEXT NOT NULL
```

### Bảng: cong_viec
```
id                INTEGER PRIMARY KEY AUTOINCREMENT
tieu_de           TEXT NOT NULL
mo_ta             TEXT
ngay_bat_dau      TEXT
han_hoan_thanh    TEXT
trang_thai        TEXT
muc_do_uu_tien    TEXT
nguoi_dung_id     INTEGER (Foreign Key -> nguoi_dung.id)
loai_id           INTEGER
```

---

## 🎯 Tài khoản demo:

Sau khi tạo database, bạn có thể đăng nhập bằng:

```
Username: admin
Password: admin123
```

---

## 📝 Lưu ý:

1. **Backup database cũ** (nếu có dữ liệu quan trọng):
   - Copy file `app/src/main/assets/BTL.db` ra nơi khác trước khi xóa

2. **Sau khi tạo database mới**:
   - Uninstall app cũ trên emulator/thiết bị
   - Rebuild project trong Android Studio
   - Chạy lại app

3. **Nếu vẫn lỗi**:
   - Xóa app data: Settings > Apps > BTL > Clear Data
   - Hoặc uninstall và cài lại

---

**Khuyến nghị**: Dùng Cách 3 (để app tự tạo) - đơn giản và nhanh nhất!
