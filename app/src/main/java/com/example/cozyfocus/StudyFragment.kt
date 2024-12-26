package com.example.cozyfocus

import com.google.android.material.tabs.TabLayout
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cozyfocus.R
import com.example.cozyfocus.model.Progress

class StudyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnReset: ImageButton
    private lateinit var backgroundImageView: ImageView
    private lateinit var arrowButton: ImageButton
    private lateinit var lockIcon: ImageView
    private lateinit var bgNameTextView: TextView
    private var timer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var initialTimerDuration = 5 * 1000L
    private var currentTimerDuration = initialTimerDuration
    private var currentTabIndex = 0
    private var currentBackgroundIndex = 0
    private var currentLevel = 1 // Level saat ini

    // Daftar latar belakang dan musik terkait
    private val backgrounds = listOf(
        Pair("Rain", Pair(R.drawable.bg, R.raw.rain)),
        Pair("Sky", Pair(R.drawable.sky, R.raw.sky)),
        Pair("Sea", Pair(R.drawable.sea, R.raw.sea)),
        Pair("Cafe", Pair(R.drawable.cafe, R.raw.cafe))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        backgroundImageView = view.findViewById(R.id.backgroundImageView)
        lockIcon = view.findViewById(R.id.lockIcon)
        bgNameTextView = view.findViewById(R.id.bg_name)
        arrowButton = view.findViewById(R.id.arrow_button)
        tabLayout = view.findViewById(R.id.tabLayout)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnStart = view.findViewById(R.id.btnStart)
        btnReset = view.findViewById(R.id.btnReset)

        // Fetch user level from Firestore (pastikan levelnya tersimpan)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        fetchUserLevel(userId)

        // Button untuk mengganti background
        arrowButton.setOnClickListener {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.size
            updateBackground()
        }

        // Update background dan musik saat awal
        updateBackground()

        // Tab selected listener
        tabLayout.addTab(tabLayout.newTab().setText("Focus"))
        tabLayout.addTab(tabLayout.newTab().setText("Break"))
        tabLayout.addTab(tabLayout.newTab().setText("Rest"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position ?: 0
                updateTimerDuration(currentTabIndex)
                stopTimer()
                resetTimer()
                updateTimerDuration(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnStart.setOnClickListener {
            if (btnStart.text == "Start") {
                startTimer(currentTimerDuration)
            } else {
                stopTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }

        updateTimerDuration(0)
    }

    // Fetch user level from Firestore
    private fun fetchUserLevel(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("progress")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val progress = document.toObject(Progress::class.java)
                    progress?.let {
                        currentLevel = it.level
                        updateBackground() // Memperbarui background berdasarkan level
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("StudyFragment", "Error fetching user level: ${e.message}", e)
            }
    }

    // Update background dan musik saat user naik level
    private fun updateBackground() {
        // Dapatkan latar belakang dan musik sesuai dengan index background
        val (name, pair) = backgrounds[currentBackgroundIndex]
        val (bgResId, songResId) = pair

        // Set background image
        backgroundImageView.setImageResource(bgResId)
        playMusic(songResId)

        // Set nama latar belakang
        bgNameTextView.text = name

        // Menyembunyikan atau menampilkan ikon kunci berdasarkan level
        if (currentBackgroundIndex >= currentLevel) {
            lockIcon.visibility = View.VISIBLE  // Latar belakang terkunci
        } else {
            lockIcon.visibility = View.GONE  // Latar belakang sudah dibuka
        }
    }

    private fun playMusic(songResId: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songResId)
        mediaPlayer?.start()
    }

    private fun startTimer(duration: Long) {
        timer?.cancel()
        btnStart.text = "Stop"

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimerDuration = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                resetTimer()
                btnStart.text = "Start"
                updateTimerText(0)
                moveToNextTab()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        btnStart.text = "Start"
        updateTimerText(currentTimerDuration)
    }

    private fun resetTimer() {
        stopTimer()
        tvTimer.text = "00:05"  // Reset timer display ke default
        currentTimerDuration = 5 * 1000L
        updateTimerText(currentTimerDuration)
    }

    private fun updateTimerText(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateTimerDuration(tabIndex: Int) {
        // Update the timer duration based on the selected tab index (Focus, Break, etc.)
        when (tabIndex) {
            0 -> { /* Set duration for Focus tab */ }
            1 -> { /* Set duration for Break tab */ }
            2 -> { /* Set duration for Rest tab */ }
        }
    }

    private fun moveToNextTab() {
        currentTabIndex = when (currentTabIndex) {
            0 -> 1
            1 -> 2
            2 -> 0
            else -> 0
        }

        val selectedTab = tabLayout.getTabAt(currentTabIndex)
        selectedTab?.select()

        updateTimerDuration(currentTabIndex)
        resetTimer()
        startTimer(currentTimerDuration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

