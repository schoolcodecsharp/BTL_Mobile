# Ứng dụng Quản lý Công việc (Task Manager)

## Tính năng chính

### 1. Xác thực người dùng
- **Đăng nhập**: Đăng nhập với tài khoản từ database BTL.db
- **Đăng ký**: Tạo tài khoản mới với validation đầy đủ
- **Splash Screen**: Màn hình chào mừng với auto-login
- **Lưu phiên đăng nhập**: Tự động đăng nhập khi mở lại app

### 2. Quản lý công việc (CRUD đầy đủ)
- **Create**: Thêm công việc mới với đầy đủ thông tin
  - Tiêu đề
  - Mô tả chi tiết
  - Hạn hoàn thành (Date Picker)
  - Mức độ ưu tiên (Thấp/Trung bình/Cao)
  - Trạng thái (Chưa bắt đầu/Đang làm/Hoàn thành)

- **Read**: Xem danh sách công việc
  - Hiển thị theo thứ tự mới nhất
  - Bộ lọc: Tất cả/Đang làm/Hoàn thành
  - Hiển thị màu sắc theo mức độ ưu tiên
  - Cảnh báo công việc quá hạn (màu đỏ)

- **Update**: Chỉnh sửa công việc
  - Cập nhật tất cả thông tin
  - Đánh dấu hoàn thành bằng checkbox
  - Tự động cập nhật thông báo

- **Delete**: Xóa công việc
  - Xác nhận trước khi xóa
  - Tự động hủy thông báo liên quan

### 3. Hệ thống thông báo thông minh
- **Thông báo tự động**: 
  - Nhắc nhở trước 1 ngày khi công việc sắp hết hạn
  - Thông báo vào lúc 9:00 sáng
  
- **Thông báo tổng quan**:
  - Kiểm tra công việc sắp hết hạn trong 3 ngày
  - Hiển thị số lượng công việc cần chú ý

- **Quản lý thông báo**:
  - Tự động hủy khi hoàn thành công việc
  - Tự động hủy khi xóa công việc
  - Cập nhật khi thay đổi hạn hoàn thành
  - Khôi phục thông báo sau khi khởi động lại thiết bị

### 4. Tìm kiếm và lọc
- **Tìm kiếm thông minh**: 
  - Tìm theo tiêu đề
  - Tìm theo mô tả
  - Kết quả real-time

- **Bộ lọc nâng cao**:
  - Lọc theo trạng thái
  - Lọc theo mức độ ưu tiên
  - Lọc theo thời gian

### 5. Thống kê chi tiết
- **Tổng quan**:
  - Tổng số công việc
  - Số công việc hoàn thành
  - Số công việc đang làm
  - Số công việc chưa bắt đầu
  - Số công việc quá hạn

- **Phân tích**:
  - Tỷ lệ hoàn thành (%)
  - Thống kê theo mức độ ưu tiên
  - Biểu đồ trực quan với màu sắc

### 6. Cài đặt và tùy chỉnh
- **Giao diện**:
  - Chế độ tối (Dark Mode)
  - Tùy chỉnh theme

- **Thông báo**:
  - Cài đặt thông báo hệ thống
  - Tùy chỉnh thời gian nhắc nhở

- **Dữ liệu**:
  - Sao lưu database
  - Khôi phục từ bản sao lưu
  - Xóa tất cả dữ liệu

### 7. Giao diện người dùng
- **Material Design 3**: Giao diện hiện đại, đẹp mắt
- **Bottom Navigation**: Điều hướng dễ dàng
- **Màu sắc phân biệt**:
  - Đỏ: Ưu tiên cao
  - Vàng: Ưu tiên trung bình
  - Xanh: Ưu tiên thấp
  
- **Responsive**: Tối ưu cho mọi kích thước màn hình
- **Animations**: Chuyển động mượt mà
- **Icons**: Biểu tượng trực quan

### 8. Trang Profile
- **Thông tin cá nhân**: Hiển thị username và email
- **Thống kê nhanh**: 
  - Tổng số công việc
  - Số công việc đã hoàn thành
- **Đăng xuất**: An toàn và xóa phiên đăng nhập

## Cấu trúc Database (BTL.db)

### Bảng nguoi_dung
- id (INTEGER PRIMARY KEY)
- ten_dang_nhap (TEXT)
- email (TEXT)
- mat_khau (TEXT)

### Bảng cong_viec
- id (INTEGER PRIMARY KEY)
- tieu_de (TEXT)
- mo_ta (TEXT)
- ngay_bat_dau (TEXT)
- han_hoan_thanh (TEXT)
- trang_thai (TEXT)
- muc_do_uu_tien (TEXT)
- nguoi_dung_id (INTEGER)
- loai_id (INTEGER)

## Hướng dẫn cài đặt

1. **Copy database vào assets**:
   - File BTL.db đã được copy vào `app/src/main/assets/`
   - App sẽ tự động copy database khi chạy lần đầu

2. **Rebuild project**:
   ```
   Build > Rebuild Project
   ```

3. **Gỡ cài đặt app cũ** (nếu có):
   - Để đảm bảo database mới được sử dụng

4. **Chạy app**:
   - Chọn thiết bị/emulator
   - Run > Run 'app'

5. **Cấp quyền thông báo**:
   - Khi app yêu cầu, chọn "Allow" để nhận thông báo

## Quyền cần thiết

- `POST_NOTIFICATIONS`: Hiển thị thông báo (Android 13+)
- `SCHEDULE_EXACT_ALARM`: Lên lịch thông báo chính xác
- `USE_EXACT_ALARM`: Sử dụng alarm chính xác
- `BOOT_COMPLETED`: Khôi phục thông báo sau khi khởi động lại

## Công nghệ sử dụng

- **Kotlin**: Ngôn ngữ lập trình chính
- **Android SDK**: Framework phát triển
- **SQLite**: Database local
- **Material Components**: UI components
- **AlarmManager**: Quản lý thông báo
- **SharedPreferences**: Lưu trữ phiên đăng nhập

## Tính năng nổi bật

✅ CRUD hoàn chỉnh cho công việc
✅ Hệ thống thông báo thông minh
✅ Tìm kiếm và lọc nâng cao
✅ Thống kê chi tiết với biểu đồ
✅ Sao lưu và khôi phục dữ liệu
✅ Chế độ tối (Dark Mode)
✅ Bottom Navigation hiện đại
✅ Giao diện đẹp mắt, Material Design 3
✅ Bộ lọc và tìm kiếm linh hoạt
✅ Cảnh báo công việc quá hạn
✅ Tự động lưu phiên đăng nhập
✅ Validation đầy đủ cho form
✅ Xử lý lỗi toàn diện
✅ Responsive design
✅ Cài đặt tùy chỉnh đa dạng

## Danh sách màn hình

1. **SplashActivity** - Màn hình chào với auto-login
2. **LoginActivity** - Đăng nhập
3. **RegisterActivity** - Đăng ký tài khoản
4. **MainActivity** - Danh sách công việc với bottom nav
5. **AddTaskActivity** - Thêm công việc mới
6. **TaskDetailActivity** - Xem/Sửa/Xóa công việc
7. **SearchActivity** - Tìm kiếm công việc
8. **StatisticsActivity** - Thống kê chi tiết
9. **SettingsActivity** - Cài đặt ứng dụng
10. **ProfileActivity** - Thông tin cá nhân

## Cấu trúc dự án

```
app/
├── src/main/
│   ├── java/com/example/btl/
│   │   ├── Activities/
│   │   │   ├── SplashActivity.kt
│   │   │   ├── LoginActivity.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── AddTaskActivity.kt
│   │   │   ├── TaskDetailActivity.kt
│   │   │   ├── SearchActivity.kt
│   │   │   ├── StatisticsActivity.kt
│   │   │   ├── SettingsActivity.kt
│   │   │   └── ProfileActivity.kt
│   │   ├── Adapters/
│   │   │   └── TaskAdapter.kt
│   │   ├── Models/
│   │   │   ├── Task.kt
│   │   │   └── Category.kt
│   │   ├── Database/
│   │   │   └── DatabaseHelper.kt
│   │   ├── Notifications/
│   │   │   ├── NotificationHelper.kt
│   │   │   ├── NotificationReceiver.kt
│   │   │   └── BootReceiver.kt
│   │   └── Utils/
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│   │   └── xml/
│   └── assets/
│       └── BTL.db
└── build.gradle.kts
```

## Lưu ý

- Database phải có cấu trúc đúng như mô tả
- Cần cấp quyền thông báo để sử dụng đầy đủ tính năng
- Định dạng ngày: yyyy-MM-dd
- App yêu cầu Android 5.0 (API 21) trở lên

## Tác giả

Ứng dụng được phát triển cho môn Lập trình di động
