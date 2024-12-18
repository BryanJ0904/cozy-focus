package com.example.cozyfocus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cozyfocus.model.Progress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ThirdFragment : Fragment(R.layout.fragment_third) {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var tvLevel: TextView
    private lateinit var tvTasks: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvInstruction: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        tvLevel = view.findViewById(R.id.tv_level)
        tvTasks = view.findViewById(R.id.tv_tasks)
        progressBar = view.findViewById(R.id.progress_circle)
        tvInstruction = view.findViewById(R.id.tv_instruction)

        // Ensure the user has a progress document or create one if it doesn't exist
        userId?.let { checkAndCreateProgressDocument(it) }

        return view
    }

    // Check if the user already has a progress document, if not, create one
    private fun checkAndCreateProgressDocument(userId: String) {
        db.collection("progress")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, load progress data
                    val progress = document.toObject(Progress::class.java)
                    progress?.let {
                        // Calculate required tasks for the current level
                        val requiredTasks = calculateRequiredTasks(it.level)
                        val remainingTasks = requiredTasks - it.completedTasks

                        tvLevel.text = "Level ${it.level}"
                        tvTasks.text = "${it.completedTasks}/$requiredTasks"
                        progressBar.max = requiredTasks
                        progressBar.progress = it.completedTasks

                        tvInstruction.text = "Finish $remainingTasks more tasks to level up. Next level: ${it.level + 1}"
                    }
                } else {
                    // Document does not exist, create a new one
                    createProgressDocument(userId)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ThirdFragment", "Error getting progress document: ${e.message}", e)
            }
    }

    // Create or initialize a progress document for a new user
    private fun createProgressDocument(userId: String) {
        val progress = Progress(level = 1, completedTasks = 0)

        db.collection("progress")
            .document(userId)
            .set(progress)
            .addOnSuccessListener {
                // Set initial progress
                tvLevel.text = "Level 1"
                tvTasks.text = "0/5"
                progressBar.progress = 0

                tvInstruction.text = "Finish 5 tasks to level up. Next level: 2"
            }
            .addOnFailureListener { e ->
                Log.e("ThirdFragment", "Error creating progress document: ${e.message}", e)
            }
    }

    // Calculate the number of tasks required for the next level
    private fun calculateRequiredTasks(level: Int): Int {
        // Define base number of tasks per level
        val baseTasksPerLevel = 5

        // Calculate the required tasks for the current level
        var requiredTasks = baseTasksPerLevel * level

        // Increase the number of tasks per level based on level milestones
        if (level % 10 == 0) {
            // If level is a multiple of 10, increase the required tasks by 5 more
            requiredTasks += 5
        }

        return requiredTasks
    }
}
