package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        currentUserId = intent.getIntExtra("USER_ID", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)

        val ivBackToMain = findViewById<android.widget.ImageView>(R.id.ivBackToMain)
        val tvUsername = findViewById<TextView>(R.id.tvProfileUsername)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvTotalTasks = findViewById<TextView>(R.id.tvTotalTasks)
        val tvCompletedTasks = findViewById<TextView>(R.id.tvCompletedTasks)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Back button
        ivBackToMain.setOnClickListener {
            finish() // Quay về MainActivity
        }

        // Load user info
        loadUserInfo(tvUsername, tvEmail)
        
        // Load statistics
        loadStatistics(tvTotalTasks, tvCompletedTasks)

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserInfo(tvUsername: TextView, tvEmail: TextView) {
        try {
            val cursor = dbHelper.getUserById(currentUserId)
            if (cursor.moveToFirst()) {
                val username = cursor.getString(cursor.getColumnIndexOrThrow("ten_dang_nhap"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                
                tvUsername.text = username
                tvEmail.text = email
            }
            cursor.close()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải thông tin: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStatistics(tvTotal: TextView, tvCompleted: TextView) {
        try {
            val cursor = dbHelper.getCongViecByUserId(currentUserId)
            var total = 0
            var completed = 0

            if (cursor.moveToFirst()) {
                do {
                    total++
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                    if (status == "Hoàn thành") {
                        completed++
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            tvTotal.text = total.toString()
            tvCompleted.text = completed.toString()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải thống kê: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                logout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun logout() {
        // Xóa trạng thái đăng nhập
        getSharedPreferences("BTL_PREFS", MODE_PRIVATE).edit()
            .putBoolean("IS_LOGGED_IN", false)
            .remove("CURRENT_USER_ID")
            .remove("USER_NAME")
            .apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
