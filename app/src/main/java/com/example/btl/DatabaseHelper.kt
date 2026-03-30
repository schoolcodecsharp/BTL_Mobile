package com.example.btl

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BTL.db"
        private const val DATABASE_VERSION = 1
    }

    init {
        copyDatabaseFromAssets()
    }

    private fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        
        // Chỉ copy nếu database chưa tồn tại
        if (!dbPath.exists()) {
            try {
                dbPath.parentFile?.mkdirs()
                
                val inputStream = context.assets.open(DATABASE_NAME)
                val outputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                
                Log.d("DatabaseHelper", "Đã copy database từ assets thành công!")
            } catch (e: IOException) {
                Log.e("DatabaseHelper", "Lỗi copy database từ assets", e)
            }
        } else {
            Log.d("DatabaseHelper", "Database đã tồn tại")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Không cần tạo bảng vì đã có sẵn trong file BTL.db
        Log.d("DatabaseHelper", "onCreate called")
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DatabaseHelper", "onUpgrade called: $oldVersion -> $newVersion")
    }

    fun checkLogin(username: String, password: String): Cursor? {
        return try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE ten_dang_nhap = ? AND mat_khau = ?", arrayOf(username, password))
            Log.d("DatabaseHelper", "checkLogin: username=$username, found=${cursor.count} records")
            cursor
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi checkLogin", e)
            null
        }
    }

    fun isUsernameExists(username: String): Boolean {
        return try {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE ten_dang_nhap = ?", arrayOf(username))
            val exists = cursor.count > 0
            cursor.close()
            exists
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi kiểm tra username", e)
            false
        }
    }

    fun addUser(username: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("ten_dang_nhap", username)
            put("email", email)
            put("mat_khau", password)
        }
        return db.insert("nguoi_dung", null, values)
    }

    fun getCongViecByUserId(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM cong_viec WHERE nguoi_dung_id = ? ORDER BY id DESC", arrayOf(userId.toString()))
    }

    fun addCongViec(tieuDe: String, moTa: String, ngayBatDau: String, hanHoanThanh: String, trangThai: String, uuTien: String, nguoiDungId: Int, loaiId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("tieu_de", tieuDe)
            put("mo_ta", moTa)
            put("ngay_bat_dau", ngayBatDau)
            put("han_hoan_thanh", hanHoanThanh)
            put("trang_thai", trangThai)
            put("muc_do_uu_tien", uuTien)
            put("nguoi_dung_id", nguoiDungId)
            put("loai_id", loaiId)
        }
        return db.insert("cong_viec", null, values)
    }

    fun updateCongViec(id: Int, tieuDe: String, moTa: String, hanHoanThanh: String, trangThai: String, uuTien: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("tieu_de", tieuDe)
            put("mo_ta", moTa)
            put("han_hoan_thanh", hanHoanThanh)
            put("trang_thai", trangThai)
            put("muc_do_uu_tien", uuTien)
        }
        return db.update("cong_viec", values, "id = ?", arrayOf(id.toString()))
    }

    fun getTaskById(taskId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM cong_viec WHERE id = ?", arrayOf(taskId.toString()))
    }

    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        return db.delete("cong_viec", "id = ?", arrayOf(taskId.toString()))
    }

    fun getUserById(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM nguoi_dung WHERE id = ?", arrayOf(userId.toString()))
    }

    fun clearAllTasks(userId: Int): Int {
        val db = this.writableDatabase
        return db.delete("cong_viec", "nguoi_dung_id = ?", arrayOf(userId.toString()))
    }
    
    // Hàm để force update database từ assets
    fun forceUpdateDatabase() {
        try {
            val dbPath = context.getDatabasePath(DATABASE_NAME)
            
            // Đóng database
            close()
            
            // Xóa database cũ
            if (dbPath.exists()) {
                dbPath.delete()
                Log.d("DatabaseHelper", "Đã xóa database cũ")
            }
            
            // Copy database mới
            dbPath.parentFile?.mkdirs()
            val inputStream = context.assets.open(DATABASE_NAME)
            val outputStream = FileOutputStream(dbPath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            
            Log.d("DatabaseHelper", "Đã force update database thành công!")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi force update database", e)
        }
    }
}
