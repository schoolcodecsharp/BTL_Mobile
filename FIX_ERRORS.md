# Các lỗi đã sửa

## 1. MainActivity.kt - Thiếu import LinearLayout
**Lỗi**: Unresolved reference 'LinearLayout'
**Nguyên nhân**: Thiếu import android.widget.LinearLayout
**Đã sửa**: Thêm `import android.widget.LinearLayout`

## 2. build.gradle.kts - Cú pháp sai và Kotlin plugin conflict
**Lỗi 1**: `compileSdk { version = release(36) }` - cú pháp không đúng
**Lỗi 2**: "The request for this plugin could not be satisfied because the plugin is already on the classpath"
**Nguyên nhân**: 
- Sử dụng sai cú pháp cho compileSdk
- Kotlin plugin được khai báo sai cách
**Đã sửa**: 
- Đổi thành `compileSdk = 34`
- Sử dụng `kotlin("android")` thay vì `alias(libs.plugins.kotlin.android)`
- Thêm Kotlin plugin vào root build.gradle.kts với version
- Thêm kotlinOptions
- Thêm RecyclerView dependency

## 3. libs.versions.toml - Thiếu RecyclerView
**Lỗi**: Thiếu RecyclerView library
**Đã sửa**:
- Thêm `recyclerview = "1.3.2"` vào versions
- Thêm `androidx-recyclerview` library
- Kotlin plugin được khai báo trực tiếp trong build.gradle.kts thay vì qua version catalog

## 4. Root build.gradle.kts - Thiếu Kotlin plugin
**Lỗi**: Kotlin plugin chưa được khai báo ở root level
**Đã sửa**:
- Thêm `kotlin("android") version "1.9.0" apply false` vào root build.gradle.kts

## Các bước tiếp theo:

### 1. Sync Gradle
```
File > Sync Project with Gradle Files
```
Hoặc nhấn nút "Sync Now" khi Android Studio hiển thị banner

### 2. Clean và Rebuild
```
Build > Clean Project
Build > Rebuild Project
```

### 3. Kiểm tra lỗi còn lại
Nếu vẫn còn lỗi, kiểm tra:
- Android Studio đã cài đặt Kotlin plugin chưa
- SDK version 34 đã được download chưa
- Internet connection để download dependencies

### 4. Chạy ứng dụng
```
Run > Run 'app'
```

## Lưu ý quan trọng:

1. **File BTL.db**: Đảm bảo file đã được copy vào `app/src/main/assets/BTL.db`

2. **Permissions**: Khi chạy lần đầu, cấp quyền thông báo cho app

3. **Database structure**: Đảm bảo database có đúng cấu trúc:
   - Bảng `nguoi_dung` với các cột: id, ten_dang_nhap, email, mat_khau
   - Bảng `cong_viec` với các cột: id, tieu_de, mo_ta, ngay_bat_dau, han_hoan_thanh, trang_thai, muc_do_uu_tien, nguoi_dung_id, loai_id

4. **Gỡ app cũ**: Nếu đã cài app trước đó, gỡ cài đặt để database mới được sử dụng

## Nếu vẫn gặp lỗi:

### Lỗi "Cannot resolve symbol 'R'"
**Giải pháp**:
```
Build > Clean Project
File > Invalidate Caches / Restart
```

### Lỗi "Unresolved reference" cho các class
**Giải pháp**:
- Kiểm tra tất cả file .kt đã được tạo đúng package
- Sync Gradle lại
- Rebuild project

### Lỗi "Manifest merger failed"
**Giải pháp**:
- Kiểm tra AndroidManifest.xml không có duplicate activities
- Kiểm tra tất cả activity đã được khai báo

### Lỗi Database
**Giải pháp**:
- Xóa app khỏi thiết bị
- Đảm bảo BTL.db trong assets
- Cài đặt lại app

## Checklist trước khi chạy:

- [ ] Sync Gradle thành công
- [ ] Không có lỗi compile
- [ ] File BTL.db trong assets
- [ ] Tất cả activities đã khai báo trong Manifest
- [ ] Permissions đã được thêm vào Manifest
- [ ] Clean và Rebuild thành công

## Liên hệ hỗ trợ:

Nếu vẫn gặp vấn đề, cung cấp:
1. Log lỗi đầy đủ từ Build Output
2. Screenshot lỗi
3. Version Android Studio đang dùng
4. Version Kotlin plugin
