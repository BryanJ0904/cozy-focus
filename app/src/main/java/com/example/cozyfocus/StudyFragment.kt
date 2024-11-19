package com.example.cozyfocus

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class StudyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var tvTimer: TextView
    private lateinit var arrowButton: ImageButton
    private lateinit var bgNameTextView: TextView
    private lateinit var rootLayout: ConstraintLayout

    private var timer: CountDownTimer? = null
    private var currentTimerDuration = 10 * 1000L
    private var currentTabIndex = 0
    private var currentBackgroundIndex = 0
    private var mediaPlayer: MediaPlayer? = null

    private val backgrounds = listOf(
        Pair("Forest", Pair(R.drawable.forest, R.raw.forest)),
        Pair("Rain", Pair(R.drawable.bg, R.raw.rain)),
        Pair("Sea", Pair(R.drawable.sea, R.raw.sea)),
        Pair("Cafe", Pair(R.drawable.cafe, R.raw.sky)),
        Pair("Sky", Pair(R.drawable.sky, R.raw.cafe))
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()

        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tabLayout)
        tvTimer = view.findViewById(R.id.tvTimer)
        arrowButton = view.findViewById(R.id.arrow_button)
        bgNameTextView = view.findViewById(R.id.bg_name)
        rootLayout = view.findViewById(R.id.rootLayout)

        setupTabs()
        updateBackground()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab?.position ?: 0
                updateTimerDuration()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        arrowButton.setOnClickListener {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.size
            updateBackground()
        }
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Focus"))
        tabLayout.addTab(tabLayout.newTab().setText("Break"))
        tabLayout.addTab(tabLayout.newTab().setText("Rest"))
    }

    private fun updateBackground() {
        val (name, res) = backgrounds[currentBackgroundIndex]
        val (bgResId, songResId) = res
        bgNameTextView.text = name
        rootLayout.setBackgroundResource(bgResId)
        playMusic(songResId)
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

    private fun updateTimerDuration() {
        when (currentTabIndex) {
            0 -> startTimer(5 * 1000L)
            1 -> startTimer(5 * 1000L)
            2 -> startTimer(5 * 1000L)
        }
    }

    private fun startTimer(duration: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                moveToNextTab()
            }
        }.start()
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
        tabLayout.getTabAt(currentTabIndex)?.select()
        updateTimerDuration()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMusic()
    }
}
