package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {

    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        currentUserId = intent.getIntExtra("USER_ID", -1)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        // Dark Mode Toggle
        val switchDarkMode = findViewById<Switch>(R.id.switchDarkMode)
        val sharedPrefs = getSharedPreferences("BTL_PREFS", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DARK_MODE", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("DARK_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Notification Settings
        findViewById<LinearLayout>(R.id.btnNotificationSettings).setOnClickListener {
            Toast.makeText(this, "Mở cài đặt thông báo hệ thống", Toast.LENGTH_SHORT).show()
            try {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Backup Database
        findViewById<LinearLayout>(R.id.btnBackup).setOnClickListener {
            backupDatabase()
        }

        // Restore Database
        findViewById<LinearLayout>(R.id.btnRestore).setOnClickListener {
            showRestoreDialog()
        }

        // Clear Data
        findViewById<LinearLayout>(R.id.btnClearData).setOnClickListener {
            showClearDataDialog()
        }
        
        // Force Update Database
        findViewById<LinearLayout>(R.id.btnRestore).setOnLongClickListener {
            showForceUpdateDialog()
            true
        }

        // About
        findViewById<LinearLayout>(R.id.btnAbout).setOnClickListener {
            showAboutDialog()
        }

        // Profile
        findViewById<LinearLayout>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }
    }

    private fun backupDatabase() {
        try {
            val currentDB = getDatabasePath("BTL.db")
            val backupDB = File(getExternalFilesDir(null), "BTL_backup.db")

            if (currentDB.exists()) {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()

                Toast.makeText(this, "Sao lưu thành công: ${backupDB.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi sao lưu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun restoreDatabase() {
        try {
            val backupDB = File(getExternalFilesDir(null), "BTL_backup.db")
            val currentDB = getDatabasePath("BTL.db")

            if (backupDB.exists()) {
                val src = FileInputStream(backupDB).channel
                val dst = FileOutputStream(currentDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()

                Toast.makeText(this, "Khôi phục thành công! Vui lòng khởi động lại ứng dụng", Toast.LENGTH_LONG).show()
                
                // Restart app
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Không tìm thấy file sao lưu", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi khôi phục: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showRestoreDialog() {
        AlertDialog.Builder(this)
            .setTitle("Khôi phục dữ liệu")
            .setMessage("Bạn có chắc chắn muốn khôi phục dữ liệu từ bản sao lưu? Dữ liệu hiện tại sẽ bị ghi đè.")
            .setPositiveButton("Khôi phục") { _, _ ->
                restoreDatabase()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showClearDataDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xóa dữ liệu")
            .setMessage("Bạn có chắc chắn muốn xóa TẤT CẢ dữ liệu? Hành động này không thể hoàn tác!")
            .setPositiveButton("Xóa") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun clearAllData() {
        try {
            val dbHelper = DatabaseHelper(this)
            dbHelper.clearAllTasks(currentUserId)
            dbHelper.close()

            Toast.makeText(this, "Đã xóa tất cả công việc", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Về ứng dụng")
            .setMessage("Ứng dụng Quản lý Công việc\n\nPhiên bản: 1.0.0\n\nPhát triển bởi: Nhóm BTL\n\nMô tả: Ứng dụng giúp quản lý công việc hiệu quả với thông báo thông minh và giao diện đẹp mắt.")
            .setPositiveButton("Đóng", null)
            .show()
    }
    
    private fun showForceUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cập nhật Database")
            .setMessage("Bạn có muốn cập nhật database từ file assets? Điều này sẽ xóa tất cả dữ liệu hiện tại và khôi phục về dữ liệu mặc định.")
            .setPositiveButton("Cập nhật") { _, _ ->
                forceUpdateDatabase()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    
    private fun forceUpdateDatabase() {
        try {
            val dbHelper = DatabaseHelper(this)
            dbHelper.forceUpdateDatabase()
            
            Toast.makeText(this, "Đã cập nhật database! Vui lòng khởi động lại ứng dụng", Toast.LENGTH_LONG).show()
            
            // Restart app
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
