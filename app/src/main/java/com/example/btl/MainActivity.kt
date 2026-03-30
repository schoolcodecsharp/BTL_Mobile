package com.example.btl

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var notificationHelper: NotificationHelper
    private var currentUserId: Int = -1
    private var currentUserName: String? = null
    private var allTasks = mutableListOf<Task>()
    private var currentFilter = "all"
    private var currentSort = "date" // date, priority, name
    private var currentDateFilter = "all" // all, today, week, month

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkUpcomingTasks()
        } else {
            Toast.makeText(this, "Cần cấp quyền thông báo để nhận nhắc nhở", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUserId = intent.getIntExtra("USER_ID", -1)
        currentUserName = intent.getStringExtra("USER_NAME")

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Lưu userId vào SharedPreferences cho BootReceiver
        getSharedPreferences("BTL_PREFS", MODE_PRIVATE).edit()
            .putInt("CURRENT_USER_ID", currentUserId)
            .apply()

        dbHelper = DatabaseHelper(this)
        notificationHelper = NotificationHelper(this)
        
        rvTasks = findViewById(R.id.rvTasks)
        val fabAdd: FloatingActionButton = findViewById(R.id.fabAddTask)
        val tvWelcome: TextView = findViewById(R.id.tvWelcome)
        val chipAll = findViewById<com.google.android.material.chip.Chip>(R.id.chipAll)
        val chipInProgress = findViewById<com.google.android.material.chip.Chip>(R.id.chipInProgress)
        val chipDone = findViewById<com.google.android.material.chip.Chip>(R.id.chipDone)
        val ivProfile = findViewById<android.widget.ImageView>(R.id.ivProfile)

        if (currentUserName != null) {
            tvWelcome.text = "Chào, $currentUserName!"
        }

        setupRecyclerView()
        loadTasks()
        requestNotificationPermission()

        // Profile button
        ivProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        // Filter chips
        chipAll.setOnClickListener {
            currentFilter = "all"
            applyFilter()
        }

        chipInProgress.setOnClickListener {
            currentFilter = "in_progress"
            applyFilter()
        }

        chipDone.setOnClickListener {
            currentFilter = "done"
            applyFilter()
        }
        
        // Sort menu
        findViewById<android.widget.ImageView>(R.id.ivSort)?.setOnClickListener {
            showSortMenu()
        }
        
        // Date filter menu
        findViewById<android.widget.ImageView>(R.id.ivFilter)?.setOnClickListener {
            showDateFilterMenu()
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        // Bottom Navigation
        findViewById<LinearLayout>(R.id.btnNavSearch).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavStatistics).setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            startActivity(intent)
        }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    checkUpcomingTasks()
                }
                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            checkUpcomingTasks()
        }
    }

    private fun checkUpcomingTasks() {
        notificationHelper.checkAndNotifyUpcomingTasks(dbHelper, currentUserId)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(mutableListOf(), { task, isChecked ->
            // Xử lý khi tích vào checkbox
            val newStatus = if (isChecked) "Hoàn thành" else "Đang làm"
            val result = dbHelper.updateCongViec(task.id, task.tieuDe, task.moTa, task.hanHoanThanh, newStatus, task.uuTien)
            
            if (result > 0) {
                if (isChecked) {
                    // Hủy thông báo khi hoàn thành
                    notificationHelper.cancelTaskReminder(task.id)
                    Toast.makeText(this, "Đã hoàn thành: ${task.tieuDe}", Toast.LENGTH_SHORT).show()
                } else {
                    // Lên lịch lại thông báo
                    notificationHelper.scheduleTaskReminder(task.id, task.tieuDe, task.hanHoanThanh)
                }
                loadTasks()
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }, { task ->
            // Xử lý khi click vào item (Xem chi tiết/Sửa)
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        })

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun applyFilter() {
        var filtered = when (currentFilter) {
            "in_progress" -> allTasks.filter { it.trangThai == "Đang làm" || it.trangThai == "Chưa bắt đầu" }
            "done" -> allTasks.filter { it.trangThai == "Hoàn thành" }
            else -> allTasks
        }
        
        // Apply date filter
        filtered = when (currentDateFilter) {
            "today" -> filtered.filter { isToday(it.hanHoanThanh) }
            "week" -> filtered.filter { isThisWeek(it.hanHoanThanh) }
            "month" -> filtered.filter { isThisMonth(it.hanHoanThanh) }
            else -> filtered
        }
        
        // Apply sort
        filtered = when (currentSort) {
            "priority" -> filtered.sortedByDescending { getPriorityValue(it.uuTien) }
            "name" -> filtered.sortedBy { it.tieuDe }
            else -> filtered.sortedBy { it.hanHoanThanh }
        }
        
        taskAdapter.updateData(filtered)
    }
    
    private fun getPriorityValue(priority: String): Int {
        return when (priority) {
            "Cao" -> 3
            "Trung bình" -> 2
            "Thấp" -> 1
            else -> 0
        }
    }
    
    private fun isToday(dateStr: String): Boolean {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val taskDate = sdf.parse(dateStr)
            val today = java.util.Calendar.getInstance()
            val taskCal = java.util.Calendar.getInstance()
            taskCal.time = taskDate
            
            today.get(java.util.Calendar.YEAR) == taskCal.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.DAY_OF_YEAR) == taskCal.get(java.util.Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isThisWeek(dateStr: String): Boolean {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val taskDate = sdf.parse(dateStr)
            val today = java.util.Calendar.getInstance()
            val taskCal = java.util.Calendar.getInstance()
            taskCal.time = taskDate
            
            today.get(java.util.Calendar.YEAR) == taskCal.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.WEEK_OF_YEAR) == taskCal.get(java.util.Calendar.WEEK_OF_YEAR)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun isThisMonth(dateStr: String): Boolean {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val taskDate = sdf.parse(dateStr)
            val today = java.util.Calendar.getInstance()
            val taskCal = java.util.Calendar.getInstance()
            taskCal.time = taskDate
            
            today.get(java.util.Calendar.YEAR) == taskCal.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.MONTH) == taskCal.get(java.util.Calendar.MONTH)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun showSortMenu() {
        val options = arrayOf("Sắp xếp theo ngày", "Sắp xếp theo độ ưu tiên", "Sắp xếp theo tên")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sắp xếp")
            .setItems(options) { _, which ->
                currentSort = when (which) {
                    0 -> "date"
                    1 -> "priority"
                    2 -> "name"
                    else -> "date"
                }
                applyFilter()
                Toast.makeText(this, "Đã sắp xếp theo ${options[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun showDateFilterMenu() {
        val options = arrayOf("Tất cả", "Hôm nay", "Tuần này", "Tháng này")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Lọc theo thời gian")
            .setItems(options) { _, which ->
                currentDateFilter = when (which) {
                    0 -> "all"
                    1 -> "today"
                    2 -> "week"
                    3 -> "month"
                    else -> "all"
                }
                applyFilter()
                Toast.makeText(this, "Đã lọc: ${options[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun loadTasks() {
        try {
            val cursor = dbHelper.getCongViecByUserId(currentUserId)
            allTasks.clear()

            if (cursor.moveToFirst()) {
                do {
                    val task = Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tieu_de")),
                        cursor.getString(cursor.getColumnIndexOrThrow("mo_ta")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ngay_bat_dau")),
                        cursor.getString(cursor.getColumnIndexOrThrow("han_hoan_thanh")),
                        cursor.getString(cursor.getColumnIndexOrThrow("trang_thai")),
                        cursor.getString(cursor.getColumnIndexOrThrow("muc_do_uu_tien"))
                    )
                    allTasks.add(task)
                } while (cursor.moveToNext())
            }
            cursor.close()
            applyFilter()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks() // Refresh danh sách khi quay lại màn hình
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
