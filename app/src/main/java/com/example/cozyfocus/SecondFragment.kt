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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.adapter.TaskAdapter
import com.example.cozyfocus.enums.TaskStatus
import com.example.cozyfocus.model.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SecondFragment : Fragment(R.layout.fragment_second) {
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SecondFragment", "onCreate() called")
        setRetainInstance(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        taskRecyclerView = view.findViewById(R.id.taskItems)
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchTasks() // Menampilkan task berdasarkan user

        val addTaskButton = view.findViewById<LinearLayout>(R.id.addTask)
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }

        return view
    }

    // Fungsi untuk mengambil task berdasarkan userId
    private fun fetchTasks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("tasks")
                .document(userId) // Mengakses koleksi berdasarkan userId
                .collection("user_tasks") // Mengakses sub-koleksi "user_tasks" untuk task
                .get()
                .addOnSuccessListener { result ->
                    val taskList = mutableListOf<Task>()
                    for (document in result) {
                        val task = document.toObject(Task::class.java)
                        taskList.add(task)
                    }

                    taskAdapter = TaskAdapter(taskList, ::showEditTaskDialog, ::deleteTask)
                    taskRecyclerView.adapter = taskAdapter
                }
                .addOnFailureListener { exception ->
                    Log.w("SecondFragment", "Error getting tasks: ", exception)
                }
        }
    }

    // Fungsi untuk menambahkan task baru
    private fun addTask(task: Task) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("tasks")
                .document(userId) // Menyimpan task di bawah dokumen userId
                .collection("user_tasks") // Menyimpan task dalam sub-koleksi "user_tasks"
                .document(task.id)
                .set(task)
                .addOnSuccessListener {
                    Log.d("SecondFragment", "Task successfully added!")
                    fetchTasks() // Refresh the task list
                }
                .addOnFailureListener { e ->
                    Log.w("SecondFragment", "Error adding task", e)
                }
        }
    }

    // Fungsi untuk menampilkan dialog tambah task
    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_task_form, null)

        val taskTitleEditText = dialogView.findViewById<EditText>(R.id.newTaskTitle)
        val taskDateTextView = dialogView.findViewById<TextView>(R.id.newTaskDate)
        val taskStatusSpinner = dialogView.findViewById<Spinner>(R.id.newTaskStatus)

        var selectedDateTime = Calendar.getInstance()

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

        builder.setView(dialogView)
            .setTitle("Add New Task")
            .setPositiveButton("Add") { dialog, _ ->
                val taskTitle = taskTitleEditText.text.toString()
                val taskStatusPosition = taskStatusSpinner.selectedItemPosition
                val taskStatus = TaskStatus.values()[taskStatusPosition]

                val newTask = Task(
                    id = generateId(),
                    title = taskTitle,
                    date = Timestamp(selectedDateTime.time),
                    status = taskStatus.ordinal
                )

                addTask(newTask)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    // Fungsi untuk menampilkan dialog edit task
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
            .setPositiveButton("Update") { dialog, _ ->
                val updatedTaskTitle = taskTitleEditText.text.toString()
                val updatedTaskStatus = TaskStatus.values()[taskStatusSpinner.selectedItemPosition]

                val updatedTask = task.copy(
                    title = updatedTaskTitle,
                    date = Timestamp(selectedDateTime.time),
                    status = updatedTaskStatus.ordinal
                )

                editTask(updatedTask)
                dialog.dismiss()
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
                    fetchTasks() // Refresh the task list
                }
                .addOnFailureListener { e ->
                    Log.w("SecondFragment", "Error updating task", e)
                }
        }
    }

    // Fungsi untuk menghapus task
    private fun deleteTask(task: Task) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("tasks")
                .document(userId) // Mengakses koleksi berdasarkan userId
                .collection("user_tasks") // Mengakses sub-koleksi "user_tasks"
                .document(task.id)
                .delete()
                .addOnSuccessListener {
                    fetchTasks() // Refresh the task list
                }
                .addOnFailureListener { e ->
                    Log.w("SecondFragment", "Error deleting task", e)
                }
        }
    }

    // Fungsi untuk membuat ID unik
    private fun generateId(): String {
        return System.currentTimeMillis().toString()
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
}
