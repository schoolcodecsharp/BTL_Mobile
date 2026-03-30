package com.example.btl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Khôi phục các thông báo sau khi khởi động lại thiết bị
            rescheduleNotifications(context)
        }
    }

    private fun rescheduleNotifications(context: Context) {
        try {
            val dbHelper = DatabaseHelper(context)
            val notificationHelper = NotificationHelper(context)
            
            // Lấy tất cả công việc chưa hoàn thành và lên lịch lại thông báo
            val sharedPrefs = context.getSharedPreferences("BTL_PREFS", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("CURRENT_USER_ID", -1)
            
            if (userId != -1) {
                val cursor = dbHelper.getCongViecByUserId(userId)
                if (cursor.moveToFirst()) {
                    do {
                        val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                        if (status != "Hoàn thành") {
                            val taskId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                            val title = cursor.getString(cursor.getColumnIndexOrThrow("tieu_de"))
                            val deadline = cursor.getString(cursor.getColumnIndexOrThrow("han_hoan_thanh"))
                            
                            notificationHelper.scheduleTaskReminder(taskId, title, deadline)
                        }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            dbHelper.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
