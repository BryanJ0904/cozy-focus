package com.example.cozyfocus

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
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView


class StudyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnReset: ImageButton
    private lateinit var backgroundImageView: ImageView
    private lateinit var arrowButton: ImageButton
    private lateinit var bgNameTextView: TextView
    private var timer: CountDownTimer? = null
    private var backgroundMediaPlayer: MediaPlayer? = null
    private var countdownMediaPlayer: MediaPlayer? = null
    private var initialTimerDuration = 20 * 60 * 1000L
    private var currentTimerDuration = initialTimerDuration
    private var currentTabIndex = 0
    private var currentBackgroundIndex = 0
    private lateinit var progressDots: List<ImageView>
    private var completedSteps = 0
    private var wasLastFocus = false
    var isTimerRunning = false

    private val backgrounds = listOf(
        Pair("Rain", Pair(R.drawable.bg, R.raw.rain)),
        Pair("Forest", Pair(R.drawable.forest, R.raw.forest)),
        Pair("Sea", Pair(R.drawable.sea, R.raw.sea)),
        Pair("Cafe", Pair(R.drawable.cafe, R.raw.cafe)),
        Pair("Sky", Pair(R.drawable.sky, R.raw.sky))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tabLayout)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnStart = view.findViewById(R.id.btnStart)
        btnReset = view.findViewById(R.id.btnReset)
        backgroundImageView = view.findViewById(R.id.backgroundImageView)
        arrowButton = view.findViewById(R.id.arrow_button)
        bgNameTextView = view.findViewById(R.id.bg_name)
        isTimerRunning = false

        arrowButton.setOnClickListener {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.size
            updateBackground()
        }

        tabLayout.addTab(tabLayout.newTab().setText("Focus"))
        tabLayout.addTab(tabLayout.newTab().setText("Break"))
        tabLayout.addTab(tabLayout.newTab().setText("Rest"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
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

        updateBackground()
        updateTimerDuration(0)

        progressDots = listOf(
            view.findViewById(R.id.progressDot1),
            view.findViewById(R.id.progressDot2),
            view.findViewById(R.id.progressDot3),
            view.findViewById(R.id.progressDot4)
        )
        updateProgressDots(completedSteps)
    }

    private fun updateBackground() {
        val (name, res) = backgrounds[currentBackgroundIndex]
        val (bgResId, songResId) = res
        backgroundImageView.setImageResource(bgResId)
        playBackgroundMusic(songResId)
        bgNameTextView.text = name
    }

    private fun playBackgroundMusic(songResId: Int) {
        backgroundMediaPlayer?.stop()
        backgroundMediaPlayer?.release()
        backgroundMediaPlayer = MediaPlayer.create(context, songResId)
        backgroundMediaPlayer?.isLooping = true
        backgroundMediaPlayer?.start()
    }

    private fun stopBackgroundMusic() {
        backgroundMediaPlayer?.stop()
        backgroundMediaPlayer?.release()
        backgroundMediaPlayer = null
    }

    private fun playCountdownMusic() {
        countdownMediaPlayer?.stop()
        countdownMediaPlayer?.release()
        countdownMediaPlayer = MediaPlayer.create(context, R.raw.countdown)
        countdownMediaPlayer?.setOnCompletionListener {
            it.release()
            countdownMediaPlayer = null
        }
        countdownMediaPlayer?.start()
    }

    private fun updateTimerDuration(tabIndex: Int) {
        currentTabIndex = tabIndex
        currentTimerDuration = when (tabIndex) {
            0 -> 20 * 60 * 1000L  // Focus: 20 minutes
            1 -> 5 * 60 * 1000L   // Break: 5 minutes
            2 -> 15 * 60 * 1000L  // Rest: 15 minutes
            else -> 5 * 1000L     // Default duration (fallback)
        }
    }

    private fun startTimer(duration: Long) {
        toggleBottomNavigationView(false)
        isTimerRunning = true
        timer?.cancel()
        btnStart.text = "Stop"
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTimerDuration = millisUntilFinished
                updateTimerText(millisUntilFinished)

                if (millisUntilFinished in 3000..3999) {
                    playCountdownMusic()
                }
            }

            override fun onFinish() {
                btnStart.text = "Start"
                updateTimerText(0)
                toggleBottomNavigationView(true)
                moveToNextTab()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        btnStart.text = "Start"
        updateTimerText(currentTimerDuration)
        toggleBottomNavigationView(true)
    }

    private fun resetTimer() {
        stopTimer()
        isTimerRunning = false
        currentTimerDuration = initialTimerDuration
        updateTimerText(currentTimerDuration)
    }

    private fun toggleBottomNavigationView(isVisible: Boolean) {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun updateTimerText(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun moveToNextTab() {
        val nextTabIndex = when (currentTabIndex) {
            0 -> {  // From Focus
                if (wasLastFocus) {
                    completedSteps = minOf(completedSteps + 1, 4)  // Ensure we don't exceed 4 steps
                    updateProgressDots(completedSteps)
                    2  // Go to Rest
                } else {
                    completedSteps = minOf(completedSteps + 1, 4)
                    updateProgressDots(completedSteps)
                    1  // Go to Break
                }
            }
            1 -> {  // From Break
                completedSteps = minOf(completedSteps + 1, 4)
                updateProgressDots(completedSteps)
                wasLastFocus = true
                0    // Go back to Focus
            }
            2 -> {  // From Rest
                completedSteps = 0  // Reset progress when Rest is done
                updateProgressDots(completedSteps)
                wasLastFocus = false
                0    // Go back to Focus
            }
            else -> 0
        }

        currentTabIndex = nextTabIndex
        tabLayout.getTabAt(currentTabIndex)?.select()

        updateTimerDuration(currentTabIndex)
        resetTimer()
        startTimer(currentTimerDuration)
    }

    private fun updateProgressDots(completedSteps: Int) {
        Log.d("StudyFragment", "Updating progress dots: completedSteps = $completedSteps")
        activity?.runOnUiThread {
            progressDots.forEachIndexed { index, dot ->
                dot.setBackgroundResource(
                    if (index < completedSteps) R.drawable.progress_dot_active
                    else R.drawable.progress_dot_inactive
                )
            }
        }
    }

    fun showNavigationAlert(menuItemId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Navigation")
            .setMessage("Are you sure you want to leave? This action will reset the timer.")
            .setPositiveButton("Yes") { _, _ ->
                stopTimer() // Reset the timer
                (activity as? MainActivity)?.navigateTo(menuItemId) // Navigate to the selected page
            }
            .setNegativeButton("No", null) // Do nothing
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        stopBackgroundMusic()
        countdownMediaPlayer?.release()
        countdownMediaPlayer = null
        Log.d("StudyFragment", "onDestroyView called: Cleaning up resources")
    }
}
