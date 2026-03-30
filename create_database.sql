-- Script tạo database BTL.db với cấu trúc đúng

-- Xóa bảng cũ nếu có
DROP TABLE IF EXISTS cong_viec;
DROP TABLE IF EXISTS nguoi_dung;

-- Tạo bảng nguoi_dung
CREATE TABLE nguoi_dung (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ten_dang_nhap TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    mat_khau TEXT NOT NULL
);

-- Tạo bảng cong_viec
CREATE TABLE cong_viec (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tieu_de TEXT NOT NULL,
    mo_ta TEXT,
    ngay_bat_dau TEXT,
    han_hoan_thanh TEXT,
    trang_thai TEXT,
    muc_do_uu_tien TEXT,
    nguoi_dung_id INTEGER,
    loai_id INTEGER,
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

-- Thêm tài khoản demo
INSERT INTO nguoi_dung (ten_dang_nhap, email, mat_khau) 
VALUES ('admin', 'admin@example.com', 'admin123');

-- Thêm một số công việc mẫu cho tài khoản admin (id=1)
INSERT INTO cong_viec (tieu_de, mo_ta, ngay_bat_dau, han_hoan_thanh, trang_thai, muc_do_uu_tien, nguoi_dung_id, loai_id)
VALUES 
('Hoàn thành báo cáo', 'Viết báo cáo tổng kết tháng', '2026-03-12', '2026-03-15', 'Đang làm', 'Cao', 1, 1),
('Họp team', 'Họp review sprint', '2026-03-13', '2026-03-13', 'Chưa bắt đầu', 'Trung bình', 1, 2),
('Học Kotlin', 'Hoàn thành khóa học Kotlin', '2026-03-10', '2026-03-20', 'Đang làm', 'Thấp', 1, 3);
