package com.example.btl

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        currentUserId = intent.getIntExtra("USER_ID", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)

        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBackFromStats)
        
        ivBack.setOnClickListener {
            finish()
        }

        loadStatistics()
    }

    private fun loadStatistics() {
        try {
            val cursor = dbHelper.getCongViecByUserId(currentUserId)
            
            var totalTasks = 0
            var completedTasks = 0
            var inProgressTasks = 0
            var highPriorityTasks = 0
            var mediumPriorityTasks = 0
            var lowPriorityTasks = 0

            if (cursor.moveToFirst()) {
                do {
                    totalTasks++
                    
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                    val priority = cursor.getString(cursor.getColumnIndexOrThrow("muc_do_uu_tien"))
                    
                    // Count by status
                    when (status) {
                        "Hoàn thành" -> completedTasks++
                        "Đang làm", "Chưa bắt đầu" -> inProgressTasks++
                    }
                    
                    // Count by priority
                    when (priority) {
                        "Cao" -> highPriorityTasks++
                        "Trung bình" -> mediumPriorityTasks++
                        "Thấp" -> lowPriorityTasks++
                    }
                    
                } while (cursor.moveToNext())
            }
            cursor.close()

            // Update UI
            findViewById<TextView>(R.id.tvTotalTasks).text = totalTasks.toString()
            findViewById<TextView>(R.id.tvCompletedTasks).text = completedTasks.toString()
            findViewById<TextView>(R.id.tvInProgressTasks).text = inProgressTasks.toString()
            
            findViewById<TextView>(R.id.tvHighPriority).text = highPriorityTasks.toString()
            findViewById<TextView>(R.id.tvMediumPriority).text = mediumPriorityTasks.toString()
            findViewById<TextView>(R.id.tvLowPriority).text = lowPriorityTasks.toString()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải thống kê: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
