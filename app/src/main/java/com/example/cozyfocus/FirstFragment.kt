package com.example.cozyfocus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cozyfocus.adapter.TaskAdapter
import com.example.cozyfocus.model.Task

class FirstFragment : Fragment() {
    private var startButton: Button? = null

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            taskAdapter = TaskAdapter(
                tasks = listOf(startedTask),
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
