package com.example.btl

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private var currentUserId: Int = -1
    private var allTasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        currentUserId = intent.getIntExtra("USER_ID", -1)
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbHelper = DatabaseHelper(this)
        
        val ivBack = findViewById<android.widget.ImageView>(R.id.ivBackFromSearch)
        val ivClear = findViewById<android.widget.ImageView>(R.id.ivClearSearch)
        etSearch = findViewById(R.id.etSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        ivBack.setOnClickListener {
            finish()
        }
        
        ivClear.setOnClickListener {
            etSearch.text?.clear()
        }

        setupRecyclerView()
        loadAllTasks()
        setupSearch()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(mutableListOf(), { task, isChecked ->
            val newStatus = if (isChecked) "Hoàn thành" else "Đang làm"
            dbHelper.updateCongViec(task.id, task.tieuDe, task.moTa, task.hanHoanThanh, newStatus, task.uuTien)
            loadAllTasks()
        }, { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        })

        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = taskAdapter
    }

    private fun loadAllTasks() {
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
            taskAdapter.updateData(allTasks)
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                if (query.isEmpty()) {
                    taskAdapter.updateData(allTasks)
                } else {
                    val filtered = allTasks.filter { task ->
                        task.tieuDe.lowercase().contains(query) ||
                        task.moTa.lowercase().contains(query)
                    }
                    taskAdapter.updateData(filtered)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadAllTasks()
    }

    override fun onDestroy() {
        if (::dbHelper.isInitialized) {
            dbHelper.close()
        }
        super.onDestroy()
    }
}
