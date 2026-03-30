package com.example.btl

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var taskList: List<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbDone: CheckBox = itemView.findViewById(R.id.cbTaskDone)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        val context = holder.itemView.context
        
        holder.tvTitle.text = task.tieuDe
        holder.tvDescription.text = task.moTa
        holder.tvDeadline.text = "📅 ${formatDate(task.hanHoanThanh)}"
        holder.tvPriority.text = task.uuTien
        holder.tvStatus.text = task.trangThai
        holder.cbDone.isChecked = task.trangThai == "Hoàn thành"

        // Priority styling
        when (task.uuTien) {
            "Cao" -> {
                holder.tvPriority.setBackgroundResource(R.drawable.priority_badge_high)
                holder.tvPriority.setTextColor(ContextCompat.getColor(context, R.color.priority_high))
            }
            "Trung bình" -> {
                holder.tvPriority.setBackgroundResource(R.drawable.priority_badge_medium)
                holder.tvPriority.setTextColor(ContextCompat.getColor(context, R.color.priority_medium))
            }
            else -> {
                holder.tvPriority.setBackgroundResource(R.drawable.priority_badge_low)
                holder.tvPriority.setTextColor(ContextCompat.getColor(context, R.color.priority_low))
            }
        }

        // Status styling
        when (task.trangThai) {
            "Hoàn thành" -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_done))
                holder.tvTitle.paintFlags = holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            "Đang làm" -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_in_progress))
                holder.tvTitle.paintFlags = holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            else -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_not_started))
                holder.tvTitle.paintFlags = holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

        // Check overdue
        if (isOverdue(task.hanHoanThanh) && task.trangThai != "Hoàn thành") {
            holder.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.error))
            holder.tvDeadline.text = "⚠️ Quá hạn: ${formatDate(task.hanHoanThanh)}"
        } else {
            holder.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        }

        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            outputFormat.format(date ?: dateStr)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun isOverdue(dateStr: String): Boolean {
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val deadline = format.parse(dateStr)
            val today = java.util.Calendar.getInstance().time
            deadline?.before(today) ?: false
        } catch (e: Exception) {
            false
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun updateData(newList: List<Task>) {
        taskList = newList
        notifyDataSetChanged()
    }
}
