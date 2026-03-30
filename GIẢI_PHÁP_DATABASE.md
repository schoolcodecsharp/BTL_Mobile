# 🎯 GIẢI PHÁP ĐƠN GIẢN NHẤT - SỬA DATABASE

## Bạn có 2 lựa chọn:

---

## ✅ CÁCH 1: XÓA DATABASE CŨ - ĐỂ APP TỰ TẠO (KHUYẾN NGHỊ)

### Đơn giản nhất - chỉ 3 bước:

1. **Xóa file database cũ**:
   - Xóa file: `app/src/main/assets/BTL.db`
   - Hoặc đổi tên thành `BTL.db.backup`

2. **Uninstall app cũ** (nếu đã cài):
   - Trên emulator/thiết bị: Gỡ cài đặt app BTL

3. **Chạy app**:
   - Trong Android Studio: Run > Run 'app'
   - App sẽ tự động tạo database mới
   - Tự động tạo tài khoản: **admin / admin123**

### ✅ Xong! Đăng nhập bằng admin/admin123

---

## ✅ CÁCH 2: SỬA DATABASE CŨ - GIỮ NGUYÊN DỮ LIỆU

### Nếu bạn muốn giữ dữ liệu cũ:

1. **Tải DB Browser for SQLite**:
   - Link: https://sqlitebrowser.org/dl/
   - Cài đặt chương trình

2. **Mở database**:
   - Mở DB Browser
   - File > Open Database
   - Chọn: `app/src/main/assets/BTL.db`

3. **Kiểm tra bảng**:
   - Xem có 2 bảng: `nguoi_dung` và `cong_viec` không?
   - Nếu thiếu, tạo bảng mới (xem hướng dẫn dưới)

4. **Kiểm tra cấu trúc bảng nguoi_dung**:
   ```
   id              INTEGER PRIMARY KEY AUTOINCREMENT
   ten_dang_nhap   TEXT NOT NULL UNIQUE
   email           TEXT NOT NULL
   mat_khau        TEXT NOT NULL
   ```

5. **Kiểm tra cấu trúc bảng cong_viec**:
   ```
   id                INTEGER PRIMARY KEY AUTOINCREMENT
   tieu_de           TEXT NOT NULL
   mo_ta             TEXT
   ngay_bat_dau      TEXT
   han_hoan_thanh    TEXT
   trang_thai        TEXT
   muc_do_uu_tien    TEXT
   nguoi_dung_id     INTEGER
   loai_id           INTEGER
   ```

6. **Nếu thiếu bảng, chạy SQL này**:
   - Chọn tab "Execute SQL"
   - Copy paste đoạn SQL từ file `create_database.sql`
   - Nhấn Execute

7. **Lưu database**:
   - File > Write Changes (Ctrl + S)

8. **Chạy app**:
   - Uninstall app cũ
   - Run app mới

---

## 🔍 CÁCH 3: ĐỂ APP TỰ SỬA (THÔNG MINH)

### App đã được lập trình để tự sửa:

1. **Giữ nguyên database cũ** trong `app/src/main/assets/BTL.db`

2. **Uninstall app cũ** trên emulator/thiết bị

3. **Chạy app**:
   - App sẽ tự động:
     - Copy database từ assets
     - Kiểm tra có thiếu bảng không
     - Tự động tạo bảng thiếu
     - Tự động thêm tài khoản admin nếu chưa có

4. **Đăng nhập**:
   - Nếu database cũ có tài khoản: dùng tài khoản đó
   - Nếu không có: dùng admin/admin123

---

## 🎯 KHUYẾN NGHỊ:

### Nếu database cũ KHÔNG có dữ liệu quan trọng:
→ Dùng **CÁCH 1** (xóa và để app tự tạo)

### Nếu database cũ CÓ dữ liệu quan trọng:
→ Dùng **CÁCH 3** (để app tự sửa)

### Nếu muốn kiểm soát hoàn toàn:
→ Dùng **CÁCH 2** (sửa thủ công bằng DB Browser)

---

## 📝 Lưu ý:

1. **Luôn uninstall app cũ** trước khi chạy app mới
2. **Hoặc Clear Data**: Settings > Apps > BTL > Clear Data
3. **Backup database cũ** trước khi xóa (nếu cần)

---

## ✅ Sau khi xong:

1. Chạy app
2. Đăng nhập bằng:
   - Tài khoản cũ (nếu giữ database)
   - Hoặc admin/admin123 (nếu tạo mới)
3. Sử dụng app bình thường!

---

**Tôi khuyên dùng CÁCH 1 - đơn giản và nhanh nhất!**
