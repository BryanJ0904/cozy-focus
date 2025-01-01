package com.example.cozyfocus

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.adapter.TaskAdapter
import com.example.cozyfocus.enums.TaskStatus
import com.example.cozyfocus.model.Progress
import com.example.cozyfocus.model.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FirstFragment : Fragment() {
    private var startButton: Button? = null
    private lateinit var taskViewModel: TaskViewModel

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)
        var savedTask = taskViewModel.task.value
        val view: View = inflater.inflate(R.layout.fragment_first, container, false)

        tasksRecyclerView = view.findViewById(R.id.taskItems)
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(
            tasks = listOf(),
            onEditClick = { task -> showEditTaskDialog(task) },
            onDeleteClick = { task -> deleteTask(task) },
            onStartTask = { task -> startTask(task) }
        )
        tasksRecyclerView.adapter = taskAdapter

        val startedTask = arguments?.getSerializable("startedTask") as? Task
        if (startedTask != null) {
            savedTask = startedTask

            parentFragmentManager.setFragmentResultListener("taskKey", this) { _, result ->
                savedTask = result.getSerializable("startedTask") as? Task
            }

            taskAdapter = TaskAdapter(
                tasks = listOf(startedTask),
                onEditClick = { task -> showEditTaskDialog(task) },
                onDeleteClick = { task -> deleteTask(task) },
                onStartTask = { task -> startTask(task) }
            )
            tasksRecyclerView.adapter = taskAdapter
        }

        if (savedTask != null) {
            taskAdapter = TaskAdapter(
                tasks = savedTask?.let { listOf(it) } ?: emptyList(),
                onEditClick = { task -> showEditTaskDialog(task) },
                onDeleteClick = { task -> deleteTask(task) },
                onStartTask = { task -> startTask(task) }
            )
            tasksRecyclerView.adapter = taskAdapter
        }

        startButton = view.findViewById(R.id.button)
        startButton?.setOnClickListener {
            (activity as MainActivity).stopMusic()
            val newStudyFragment = StudyFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flFragment, newStudyFragment, "StudyFragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun showEditTaskDialog(task: Task) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_task_form, null)

        val taskTitleEditText = dialogView.findViewById<EditText>(R.id.newTaskTitle)
        val taskDateTextView = dialogView.findViewById<TextView>(R.id.newTaskDate)
        val taskStatusSpinner = dialogView.findViewById<Spinner>(R.id.newTaskStatus)

        var selectedDateTime = Calendar.getInstance().apply {
            time = task.date.toDate()
        }

        taskTitleEditText.setText(task.title)
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault())
        taskDateTextView.text = dateFormat.format(selectedDateTime.time)

        taskDateTextView.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDateTime.set(Calendar.YEAR, selectedYear)
                    selectedDateTime.set(Calendar.MONTH, selectedMonth)
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay)
                    showTimePickerDialog(taskDateTextView, selectedDateTime)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val taskStatusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TaskStatus.values().map { it.getDisplayName() }
        )
        taskStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskStatusSpinner.adapter = taskStatusAdapter

        taskStatusSpinner.setSelection(task.status)

        // Update and Delete options
        builder.setView(dialogView)
            .setTitle("Edit Task")
            .apply {
                // Check if the task status is DONE
                if (task.status == TaskStatus.DONE.ordinal) {
                    // Disable the status spinner and update button
                    taskStatusSpinner.isEnabled = false
                    taskTitleEditText.isEnabled = false // Optional: Disable editing the title
                    setPositiveButton("Update", null) // Disable the "Update" button entirely
                } else {
                    setPositiveButton("Update") { dialog, _ ->
                        val updatedTaskTitle = taskTitleEditText.text.toString()
                        val updatedTaskStatus =
                            TaskStatus.values()[taskStatusSpinner.selectedItemPosition]

                        val updatedTask = task.copy(
                            title = updatedTaskTitle,
                            date = Timestamp(selectedDateTime.time),
                            status = updatedTaskStatus.ordinal
                        )

                        editTask(updatedTask)
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Delete") { dialog, _ ->
                deleteTask(task)
                dialog.dismiss()
            }

        builder.create().show()
    }

    // Fungsi untuk menampilkan TimePickerDialog
    private fun showTimePickerDialog(taskDateTextView: TextView, selectedDateTime: Calendar) {
        val hour = selectedDateTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDateTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedDateTime.set(Calendar.MINUTE, selectedMinute)

                val dateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm a", Locale.getDefault())
                taskDateTextView.text = dateFormat.format(selectedDateTime.time)
            },
            hour, minute, false
        )
        timePickerDialog.show()
    }

    // Fungsi untuk menyimpan perubahan task
    private fun editTask(task: Task) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("tasks")
                .document(userId) // Mengakses koleksi berdasarkan userId
                .collection("user_tasks") // Mengakses sub-koleksi "user_tasks"
                .document(task.id)
                .set(task)
                .addOnSuccessListener {
                    // Update user's progress/level when the task done
                    if (task.status == TaskStatus.DONE.value) {
                        updateProgressAndLevelUp(userId)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("SecondFragment", "Error updating task", e)
                }
        }
    }

    // Update progress and level when a task is completed
    private fun updateProgressAndLevelUp(userId: String) {
        db.collection("progress")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val currentProgress = document.toObject(Progress::class.java)
                if (currentProgress != null) {
                    // Update progress (increment tasks and potentially level up)
                    val updatedProgress = updateUserProgress(currentProgress)

                    // Update the new progress
                    db.collection("progress")
                        .document(userId)
                        .set(updatedProgress)
                        .addOnSuccessListener {
                            Log.d("Second Fragment", "Updating progress successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Second Fragment", "Error updating progress: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Second Fragment", "Error getting progress for update: ${e.message}", e)
            }
    }

    private fun updateUserProgress(progress: Progress): Progress {
        val baseTasksPerLevel = 5

        // Calculate the number of tasks required for the next level
        var requiredTasks = baseTasksPerLevel * progress.level

        // Increase the number of tasks per level based on level milestones
        if (progress.level % 10 == 0) {
            // If level is a multiple of 10, increase the required tasks by 5 more
            requiredTasks += 5
        }

        // Check if the user has completed enough tasks to level up
        val newCompletedTasks = progress.completedTasks + 1

        // Check if the user reached the required tasks for the next level
        var newLevel = progress.level
        if (newCompletedTasks >= requiredTasks) {
            newLevel++
        }

        return Progress(level = newLevel, completedTasks = newCompletedTasks)
    }

    private fun deleteTask(task: Task) {

    }

    private fun startTask(task: Task) {

    }
}
