import sqlite3
import sys

db_path = "app/src/main/assets/BTL.db"

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # Lấy danh sách bảng
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    
    print("=== DANH SÁCH BẢNG ===")
    for table in tables:
        print(f"- {table[0]}")
    
    print("\n=== CẤU TRÚC CÁC BẢNG ===")
    for table in tables:
        table_name = table[0]
        print(f"\n--- Bảng: {table_name} ---")
        cursor.execute(f"PRAGMA table_info({table_name});")
        columns = cursor.fetchall()
        for col in columns:
            print(f"  {col[1]} ({col[2]})")
        
        # Đếm số dòng
        cursor.execute(f"SELECT COUNT(*) FROM {table_name};")
        count = cursor.fetchone()[0]
        print(f"  => Số dòng: {count}")
        
        # Hiển thị 3 dòng đầu nếu có
        if count > 0:
            cursor.execute(f"SELECT * FROM {table_name} LIMIT 3;")
            rows = cursor.fetchall()
            print(f"  => Dữ liệu mẫu:")
            for row in rows:
                print(f"     {row}")
    
    conn.close()
    print("\n✅ Kiểm tra database thành công!")
    
except Exception as e:
    print(f"❌ Lỗi: {e}")
    sys.exit(1)
