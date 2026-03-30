package com.example.btl

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private var currentUserId: Int = -1
    private lateinit var etDeadline: TextInputEditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        currentUserId = intent.getIntExtra("USER_ID", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        notificationHelper = NotificationHelper(this)

        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBackFromAdd)
        val etTitle = findViewById<TextInputEditText>(R.id.etTaskTitle)
        val etDescription = findViewById<TextInputEditText>(R.id.etTaskDescription)
        etDeadline = findViewById(R.id.etTaskDeadline)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatus)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancelTask)

        // Back button
        ivBack.setOnClickListener {
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

        // Date picker
        etDeadline.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
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
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val id = dbHelper.addCongViec(
                    title,
                    description,
                    currentDate,
                    deadline,
                    status,
                    priority,
                    currentUserId,
                    1
                )

                if (id > 0) {
                    // Lên lịch thông báo cho công việc mới
                    notificationHelper.scheduleTaskReminder(id.toInt(), title, deadline)
                    
                    Toast.makeText(this, "Đã thêm công việc thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Thêm công việc thất bại", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        btnCancel.setOnClickListener {
            finish()
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

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
