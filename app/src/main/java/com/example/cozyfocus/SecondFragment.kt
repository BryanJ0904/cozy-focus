package com.example.cozyfocus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.adapter.TaskAdapter
import com.example.cozyfocus.model.Task
import com.google.firebase.firestore.FirebaseFirestore

class SecondFragment:Fragment(R.layout.fragment_second) {
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        taskRecyclerView = view.findViewById(R.id.taskItems)
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchTasks()

        return view
    }

    private fun fetchTasks() {
        db.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                val taskList = mutableListOf<Task>()
                for (document in result) {
                    val task = document.toObject(Task::class.java)
                    taskList.add(task)
                }

                taskAdapter = TaskAdapter(taskList)
                taskRecyclerView.adapter = taskAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("SecondFragment", "Error getting tasks: ", exception)
            }
    }
}
