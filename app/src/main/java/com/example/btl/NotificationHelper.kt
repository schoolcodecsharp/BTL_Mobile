package com.example.btl

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "task_reminder_channel"
        const val CHANNEL_NAME = "Nhắc nhở công việc"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Thông báo nhắc nhở công việc sắp hết hạn"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskReminder(taskId: Int, taskTitle: String, deadline: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val deadlineDate = dateFormat.parse(deadline)
            
            if (deadlineDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = deadlineDate
                
                // Thông báo trước 1 ngày
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 9)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                val currentTime = System.currentTimeMillis()
                if (calendar.timeInMillis > currentTime) {
                    scheduleNotification(taskId, taskTitle, calendar.timeInMillis)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun scheduleNotification(taskId: Int, taskTitle: String, triggerTime: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", taskTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelTaskReminder(taskId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun showImmediateNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun checkAndNotifyUpcomingTasks(dbHelper: DatabaseHelper, userId: Int) {
        try {
            val cursor = dbHelper.getCongViecByUserId(userId)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance()
            val threeDaysLater = Calendar.getInstance()
            threeDaysLater.add(Calendar.DAY_OF_MONTH, 3)

            var upcomingCount = 0

            if (cursor.moveToFirst()) {
                do {
                    val status = cursor.getString(cursor.getColumnIndexOrThrow("trang_thai"))
                    if (status != "Hoàn thành") {
                        val deadlineStr = cursor.getString(cursor.getColumnIndexOrThrow("han_hoan_thanh"))
                        val deadline = dateFormat.parse(deadlineStr)
                        
                        if (deadline != null) {
                            val deadlineCalendar = Calendar.getInstance()
                            deadlineCalendar.time = deadline
                            
                            if (deadlineCalendar.after(today) && deadlineCalendar.before(threeDaysLater)) {
                                upcomingCount++
                            }
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            if (upcomingCount > 0) {
                showImmediateNotification(
                    "Nhắc nhở công việc",
                    "Bạn có $upcomingCount công việc sắp hết hạn trong 3 ngày tới!"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
