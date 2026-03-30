# Hướng dẫn chạy ứng dụng trong Android Studio

## ⚠️ Vấn đề hiện tại:

Bạn đang gặp lỗi: **"No Java compiler found, please ensure you are running Gradle with a JDK"**

Nguyên nhân: Android Studio đang sử dụng JRE (Java Runtime Environment) thay vì JDK (Java Development Kit)

## ✅ Giải pháp:

### Cách 1: Cấu hình JDK trong Android Studio (KHUYẾN NGHỊ)

1. **Mở Android Studio**

2. **Vào Settings**:
   - Windows: `File > Settings`
   - Mac: `Android Studio > Preferences`

3. **Chọn JDK**:
   - Đi đến: `Build, Execution, Deployment > Build Tools > Gradle`
   - Tìm mục "Gradle JDK"
   - Chọn một trong các option:
     - **Embedded JDK** (Khuyến nghị - Android Studio đã có sẵn)
     - **JDK 11** hoặc **JDK 17** nếu đã cài đặt

4. **Apply và OK**

5. **Sync Gradle**:
   - Nhấn "Sync Now" ở banner trên cùng
   - Hoặc: `File > Sync Project with Gradle Files`

6. **Rebuild Project**:
   ```
   Build > Clean Project
   Build > Rebuild Project
   ```

7. **Chạy app**:
   ```
   Run > Run 'app'
   ```
   Hoặc nhấn **Shift + F10**

### Cách 2: Sử dụng Embedded JDK của Android Studio

1. **Mở File > Project Structure**

2. **Chọn SDK Location**

3. **Tìm "JDK location"**

4. **Chọn Embedded JDK**:
   - Thường ở: `C:\Program Files\Android\Android Studio\jbr`
   - Hoặc: `C:\Users\[YourName]\AppData\Local\Android\Sdk\jdk`

5. **Apply và OK**

6. **Sync và Rebuild**

### Cách 3: Cài đặt JDK mới (nếu cần)

1. **Download JDK 17**:
   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/#java17
   - OpenJDK: https://adoptium.net/

2. **Cài đặt JDK**

3. **Cấu hình trong Android Studio** (theo Cách 1)

4. **Chọn JDK vừa cài**

## 🔍 Kiểm tra JDK hiện tại:

### Trong Android Studio:

1. Mở Terminal trong Android Studio (Alt + F12)

2. Chạy lệnh:
   ```
   java -version
   ```

3. Nếu thấy "java version" → OK
   Nếu thấy lỗi → Cần cấu hình lại

### Kiểm tra Gradle JDK:

1. Mở `File > Settings > Build Tools > Gradle`

2. Xem "Gradle JDK" đang chọn gì

3. Nếu là "JRE" → Đổi sang "JDK" hoặc "Embedded JDK"

## 📱 Sau khi sửa xong:

### 1. Sync Gradle:
```
File > Sync Project with Gradle Files
```

### 2. Clean Project:
```
Build > Clean Project
```

### 3. Rebuild Project:
```
Build > Rebuild Project
```

### 4. Chạy app:
```
Run > Run 'app'
```

## 🎯 Checklist trước khi chạy:

- [ ] Đã chọn đúng JDK (không phải JRE)
- [ ] Gradle sync thành công (không có lỗi đỏ)
- [ ] Build thành công
- [ ] Đã chọn device/emulator
- [ ] File BTL.db đã có trong assets

## 🚨 Nếu vẫn lỗi:

### Lỗi: "Gradle sync failed"
**Giải pháp**:
```
File > Invalidate Caches / Restart
Chọn "Invalidate and Restart"
```

### Lỗi: "SDK not found"
**Giải pháp**:
1. `File > Project Structure > SDK Location`
2. Chọn Android SDK path
3. Thường ở: `C:\Users\[YourName]\AppData\Local\Android\Sdk`

### Lỗi: "Cannot resolve symbol"
**Giải pháp**:
```
Build > Clean Project
File > Sync Project with Gradle Files
Build > Rebuild Project
```

### Lỗi: "Emulator not found"
**Giải pháp**:
1. `Tools > Device Manager`
2. Tạo Virtual Device mới
3. Chọn device và system image
4. Finish và Start emulator

## 📞 Hỗ trợ:

Nếu vẫn gặp vấn đề, cung cấp:
1. Screenshot lỗi
2. Log từ Build Output
3. Version Android Studio đang dùng
4. Version JDK đang dùng

## 🎉 Khi chạy thành công:

1. App sẽ mở màn hình Splash
2. Tự động chuyển đến Login (nếu chưa đăng nhập)
3. Đăng nhập bằng tài khoản trong database
4. Hoặc đăng ký tài khoản mới
5. Sử dụng app!

---

**Lưu ý**: Đảm bảo đã cấp quyền thông báo khi app yêu cầu để nhận nhắc nhở công việc!
