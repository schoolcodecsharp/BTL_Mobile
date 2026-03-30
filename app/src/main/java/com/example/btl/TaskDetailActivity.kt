package com.example.btl

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private var taskId: Int = -1
    private lateinit var etDeadline: TextInputEditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId == -1) {
            Toast.makeText(this, "Lỗi tải công việc", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        notificationHelper = NotificationHelper(this)

        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBackFromDetail)
        val ivDelete = findViewById<android.widget.ImageView>(R.id.ivDeleteTask)
        val etTitle = findViewById<TextInputEditText>(R.id.etDetailTitle)
        val etDescription = findViewById<TextInputEditText>(R.id.etDetailDescription)
        etDeadline = findViewById(R.id.etDetailDeadline)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerDetailPriority)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerDetailStatus)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateTask)
        val btnCancel = findViewById<Button>(R.id.btnCancelDetail)

        // Back button
        ivBack.setOnClickListener {
            finish()
        }
        
        // Delete button
        ivDelete.setOnClickListener {
            showDeleteConfirmDialog()
        }
        
        btnCancel.setOnClickListener {
            finish()
        }

        // Setup spinners
        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_levels,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        val statusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_levels,
            android.R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        // Load task data
        loadTaskData(etTitle, etDescription, etDeadline, spinnerPriority, spinnerStatus)

        // Date picker
        etDeadline.setOnClickListener {
            showDatePicker()
        }

        btnUpdate.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()
            val priority = spinnerPriority.selectedItem.toString()
            val status = spinnerStatus.selectedItem.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (deadline.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn hạn hoàn thành", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val result = dbHelper.updateCongViec(taskId, title, description, deadline, status, priority)
                
                if (result > 0) {
                    // Cập nhật thông báo
                    if (status == "Hoàn thành") {
                        notificationHelper.cancelTaskReminder(taskId)
                    } else {
                        notificationHelper.scheduleTaskReminder(taskId, title, deadline)
                    }
                    
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadTaskData(
        etTitle: TextInputEditText,
        etDescription: TextInputEditText,
        etDeadline: TextInputEditText,
        spinnerPriority: Spinner,
        spinnerStatus: Spinner
    ) {
        try {
            val cursor = dbHelper.getTaskById(taskId)
            if (cursor.moveToFirst()) {
                etTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow("tieu_de")))
                etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("mo_ta")))
                etDeadline.setText(cursor.getString(cursor.getColumnIndexOrThrow("han_hoan_thanh")))
                
                val priority = cursor.getString(cursor.getColumnIndexOrThrow("muc_do_uu_tien"))
                val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                
                // Set spinner selections
                val priorityPosition = resources.getStringArray(R.array.priority_levels).indexOf(priority)
                if (priorityPosition >= 0) spinnerPriority.setSelection(priorityPosition)
                
                val statusPosition = resources.getStringArray(R.array.status_levels).indexOf(status)
                if (statusPosition >= 0) spinnerStatus.setSelection(statusPosition)
            }
            cursor.close()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etDeadline.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteTask()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteTask() {
        try {
            val result = dbHelper.deleteTask(taskId)
            if (result > 0) {
                // Hủy thông báo khi xóa
                notificationHelper.cancelTaskReminder(taskId)
                
                Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
