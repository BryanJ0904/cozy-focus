package com.example.cozyfocus

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class StudyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnReset: ImageButton
    private lateinit var backgroundImageView: ImageView
    private lateinit var arrowButton: ImageButton
    private lateinit var bgNameTextView: TextView
    private var timer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var initialTimerDuration = 5 * 1000L
    private var currentTimerDuration = initialTimerDuration
    private var currentTabIndex = 0
    private var currentBackgroundIndex = 0

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
    }

    private fun updateBackground() {
        val (name, res) = backgrounds[currentBackgroundIndex]
        val (bgResId, songResId) = res
        backgroundImageView.setImageResource(bgResId)
        playMusic(songResId)

        bgNameTextView.text = name
    }

    private fun playMusic(songResId: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songResId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updateTimerDuration(tabIndex: Int) {
        currentTabIndex = tabIndex
        currentTimerDuration = 5 * 1000L
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
        currentTimerDuration = 5 * 1000L
        updateTimerText(currentTimerDuration)
    }

    private fun updateTimerText(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun moveToNextTab() {
        currentTabIndex = when (currentTabIndex) {
            0 -> 1
            1 -> 0
            2 -> 1
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
