package com.example.cozyfocus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.adapter.TaskAdapter
import com.example.cozyfocus.model.Task

class FirstFragment : Fragment() {
    private var startButton: Button? = null
    private lateinit var taskViewModel: TaskViewModel

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

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
            onEditClick = { task -> editTask(task) },
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
                onEditClick = { task -> editTask(task) },
                onDeleteClick = { task -> deleteTask(task) },
                onStartTask = { task -> startTask(task) }
            )
            tasksRecyclerView.adapter = taskAdapter
        }

        if (savedTask != null) {
            taskAdapter = TaskAdapter(
                tasks = savedTask?.let { listOf(it) } ?: emptyList(),
                onEditClick = { task -> editTask(task) },
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

    private fun editTask(task: Task) {

    }

    private fun deleteTask(task: Task) {

    }

    private fun startTask(task: Task) {

    }
}
