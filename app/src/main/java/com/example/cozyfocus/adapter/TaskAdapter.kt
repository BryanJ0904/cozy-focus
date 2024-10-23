package com.example.cozyfocus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.R
import com.example.cozyfocus.enums.TaskStatus
import com.example.cozyfocus.model.Task
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val tasks: List<Task>,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskDone)
        private val taskDate: TextView = itemView.findViewById(R.id.taskDate)
        private val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        private val taskStatus: TextView = itemView.findViewById(R.id.taskStatus)
        private val editTask: ImageView = itemView.findViewById(R.id.editTask)
        private val deleteTask: ImageView = itemView.findViewById(R.id.startTask)

        fun bind(task: Task, onEditClick: (Task) -> Unit, onDeleteClick: (Task) -> Unit) {
            taskCheckbox.isChecked = task.status == TaskStatus.DONE.ordinal
            taskDate.text = formatDate(task.date)
            taskTitle.text = task.title

            val taskStatusValue = TaskStatus.fromValue(task.status)
            taskStatus.text = taskStatusValue.getDisplayName()
            val statusTextColor = ContextCompat.getColor(taskStatus.context, taskStatusValue.getStatusColor(task.status))
            taskStatus.setTextColor(statusTextColor)


            editTask.setOnClickListener { onEditClick(task) }
            deleteTask.setOnClickListener { onDeleteClick(task) }
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
        holder.bind(tasks[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = tasks.size
}
