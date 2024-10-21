package com.example.cozyfocus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.R
import com.example.cozyfocus.model.Task
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskDone)
        private val taskDate: TextView = itemView.findViewById(R.id.taskDate)
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)

        fun bind(task: Task) {
            taskCheckbox.isChecked = task.status == 1
            taskDate.text = formatDate(task.date)
            taskTitle.text = task.title
            taskStatus.text = task.status.toString()
        }

        private fun formatDate(date: Timestamp): String {
            val dateFormatter = SimpleDateFormat("EEE, d MMMM hh:mm a", Locale.getDefault())
            return dateFormatter.format(date.toDate())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}