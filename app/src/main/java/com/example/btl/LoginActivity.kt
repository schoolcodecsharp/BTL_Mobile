package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        try {
            dbHelper = DatabaseHelper(this)
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error initializing DatabaseHelper", e)
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu", Toast.LENGTH_LONG).show()
        }

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // Kiểm tra đăng nhập với ten_dang_nhap và mat_khau khớp với file BTL.db của bạn
                val cursor = dbHelper.checkLogin(username, password)
                
                if (cursor != null && cursor.moveToFirst()) {
                    // Lấy chỉ số cột dựa trên schema thực tế của file BTL.db
                    val idIndex = cursor.getColumnIndex("id")
                    val usernameIndex = cursor.getColumnIndex("ten_dang_nhap")

                    if (idIndex != -1 && usernameIndex != -1) {
                        val userId = cursor.getInt(idIndex)
                        val userName = cursor.getString(usernameIndex)

                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                        // Lưu trạng thái đăng nhập
                        getSharedPreferences("BTL_PREFS", MODE_PRIVATE).edit()
                            .putBoolean("IS_LOGGED_IN", true)
                            .putInt("CURRENT_USER_ID", userId)
                            .putString("USER_NAME", userName)
                            .apply()

                        // Chuyển sang MainActivity và truyền ID người dùng
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        intent.putExtra("USER_NAME", userName)
                        startActivity(intent)
                        
                        // Đóng màn hình login
                        finish()
                    } else {
                        Toast.makeText(this, "Lỗi cấu trúc dữ liệu người dùng", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Nếu không khớp tk/mk thì thông báo
                    Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                }
                cursor?.close()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Login Error", e)
                Toast.makeText(this, "Lỗi hệ thống: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
