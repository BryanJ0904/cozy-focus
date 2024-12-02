package com.example.cozyfocus

import android.os.Bundle
<<<<<<< Updated upstream
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cozyfocus.R

class ThirdFragment : Fragment(R.layout.fragment_third) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }



=======
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ThirdFragment:Fragment(R.layout.fragment_third) {
    private lateinit var tvLevel: TextView
    private lateinit var tvTasks: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvInstruction: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        tvLevel = view.findViewById(R.id.tv_level)
        tvTasks = view.findViewById(R.id.tv_tasks)
        progressBar = view.findViewById(R.id.progress_circle)
        tvInstruction = view.findViewById(R.id.tv_instruction)
        fetchLevelData()
        return view
    }

    private fun fetchLevelData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val level = document.getLong("level")?.toInt() ?: 1
                var tasksCompleted = document.getLong("tasksCompleted")?.toInt() ?: 0
                var tasksRequired = document.getLong("tasksRequired")?.toInt() ?: 5

                val incrementButton = view?.findViewById<Button>(R.id.btn_increment_task)
                incrementButton?.setOnClickListener {
                    if (tasksCompleted < tasksRequired) {
                        val updatedTasksCompleted = tasksCompleted + 1

                        userRef.update("tasksCompleted", updatedTasksCompleted)
                            .addOnSuccessListener {
                                // Update local variable and UI after success
                                tasksCompleted = updatedTasksCompleted
                                updateUI(level, tasksCompleted, tasksRequired)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to update progress: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "All tasks completed!", Toast.LENGTH_SHORT).show()
                    }
                }

                updateUI(level, tasksCompleted, tasksRequired)
            }
        }
    }

    private fun updateUI(level: Int, tasksCompleted: Int, tasksRequired: Int) {
        tvLevel.text = "Level $level"
        tvTasks.text = "$tasksCompleted/$tasksRequired"
        progressBar.max = tasksRequired
        progressBar.progress = tasksCompleted
        tvInstruction.text = "Finish $tasksRequired tasks to level up."

        if (tasksCompleted >= tasksRequired) {
            levelUp(level)
        }
    }

    private fun levelUp(currentLevel: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(userId)

        userRef.update(mapOf(
            "level" to currentLevel + 1,
            "tasksCompleted" to 0,
            "tasksRequired" to (currentLevel + 1) * 5 // Increment tasks required
        )).addOnSuccessListener {
            fetchLevelData() // Refresh the UI
        }
    }
>>>>>>> Stashed changes
}
