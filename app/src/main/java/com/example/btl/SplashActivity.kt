package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Xóa trạng thái đăng nhập cũ để bắt buộc đăng nhập lại
        getSharedPreferences("BTL_PREFS", MODE_PRIVATE).edit()
            .putBoolean("IS_LOGGED_IN", false)
            .remove("CURRENT_USER_ID")
            .remove("USER_NAME")
            .apply()

        // Luôn chuyển đến LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000) // 2 giây
    }
}
