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

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
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
            var notStartedTasks = 0
            var overdueTasks = 0
            var highPriorityTasks = 0
            var mediumPriorityTasks = 0
            var lowPriorityTasks = 0
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance().time

            if (cursor.moveToFirst()) {
                do {
                    totalTasks++
                    
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                    val priority = cursor.getString(cursor.getColumnIndexOrThrow("muc_do_uu_tien"))
                    val deadlineStr = cursor.getString(cursor.getColumnIndexOrThrow("han_hoan_thanh"))
                    
                    // Count by status
                    when (status) {
                        "Hoàn thành" -> completedTasks++
                        "Đang làm" -> inProgressTasks++
                        "Chưa bắt đầu" -> notStartedTasks++
                    }
                    
                    // Count by priority
                    when (priority) {
                        "Cao" -> highPriorityTasks++
                        "Trung bình" -> mediumPriorityTasks++
                        "Thấp" -> lowPriorityTasks++
                    }
                    
                    // Count overdue
                    try {
                        val deadline = dateFormat.parse(deadlineStr)
                        if (deadline != null && deadline.before(today) && status != "Hoàn thành") {
                            overdueTasks++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                } while (cursor.moveToNext())
            }
            cursor.close()

            // Update UI
            findViewById<TextView>(R.id.tvTotalTasks).text = totalTasks.toString()
            findViewById<TextView>(R.id.tvCompletedTasks).text = completedTasks.toString()
            findViewById<TextView>(R.id.tvInProgressTasks).text = inProgressTasks.toString()
            findViewById<TextView>(R.id.tvNotStartedTasks).text = notStartedTasks.toString()
            findViewById<TextView>(R.id.tvOverdueTasks).text = overdueTasks.toString()
            
            findViewById<TextView>(R.id.tvHighPriority).text = highPriorityTasks.toString()
            findViewById<TextView>(R.id.tvMediumPriority).text = mediumPriorityTasks.toString()
            findViewById<TextView>(R.id.tvLowPriority).text = lowPriorityTasks.toString()
            
            // Calculate completion rate
            val completionRate = if (totalTasks > 0) {
                (completedTasks * 100) / totalTasks
            } else {
                0
            }
            findViewById<TextView>(R.id.tvCompletionRate).text = "$completionRate%"
            
            // Update progress bar color based on rate
            val cardCompletionRate = findViewById<MaterialCardView>(R.id.cardCompletionRate)
            when {
                completionRate >= 75 -> cardCompletionRate.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                completionRate >= 50 -> cardCompletionRate.setCardBackgroundColor(Color.parseColor("#FFC107"))
                else -> cardCompletionRate.setCardBackgroundColor(Color.parseColor("#F44336"))
            }
            
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
