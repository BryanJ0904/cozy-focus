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
    private lateinit var lockIcon: ImageView  // Tambahkan ImageView untuk lock
    private lateinit var bgNameTextView: TextView
    private var currentBackgroundIndex = 0
    private var mediaPlayer: MediaPlayer? = null  // Deklarasi mediaPlayer

    private val backgrounds = listOf(
        Pair("Rain", Pair(R.drawable.bg, R.raw.rain)),
        Pair("Forest", Pair(R.drawable.forest, R.raw.forest)),
        Pair("Sea", Pair(R.drawable.sea, R.raw.sea)),
        Pair("Cafe", Pair(R.drawable.cafe, R.raw.cafe)),
        Pair("Sky", Pair(R.drawable.sky, R.raw.sky))
    )

    private var currentTabIndex = 0
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backgroundImageView = view.findViewById(R.id.backgroundImageView)
        lockIcon = view.findViewById(R.id.lockIcon)  // Menghubungkan lockIcon
        bgNameTextView = view.findViewById(R.id.bg_name)
        arrowButton = view.findViewById(R.id.arrow_button)
        tabLayout = view.findViewById(R.id.tabLayout)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnStart = view.findViewById(R.id.btnStart)
        btnReset = view.findViewById(R.id.btnReset)

        // Button untuk mengganti background
        arrowButton.setOnClickListener {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.size
            updateBackground()
        }

        // Update background dan musik saat awal
        updateBackground()

        // Tab selected listener
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Jangan ubah background ketika tab dipilih, hanya timer yang diubah
                currentTabIndex = tab?.position ?: 0
                updateTimerDuration(currentTabIndex)
                stopTimer()
                resetTimer()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        btnStart.setOnClickListener {
            if (btnStart.text == "Start") {
                startTimer()
            } else {
                stopTimer()
            }
        }

        btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun updateBackground() {
        val (name, res) = backgrounds[currentBackgroundIndex]
        val (bgResId, songResId) = res
        backgroundImageView.setImageResource(bgResId)
        playMusic(songResId)

        // Set nama latar belakang
        bgNameTextView.text = name

        // Tampilkan ikon kunci untuk latar belakang tertentu
        if (name == "Sea" || name == "Cafe" || name == "Sky") {
            lockIcon.visibility = View.VISIBLE  // Menampilkan ikon kunci
        } else {
            lockIcon.visibility = View.GONE  // Menyembunyikan ikon kunci
        }
    }

    private fun playMusic(songResId: Int) {
        // Stop dan release media player jika ada yang sedang bermain
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songResId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    private fun startTimer() {
        val duration = 5 * 1000L // Durasi timer, misalnya 5 detik
        timer?.cancel()
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                resetTimer()
            }
        }.start()
        btnStart.text = "Stop"
    }

    private fun stopTimer() {
        timer?.cancel()
        btnStart.text = "Start"
    }

    private fun resetTimer() {
        stopTimer()
        tvTimer.text = "00:05"  // Reset timer display to default
    }

    private fun updateTimerText(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateTimerDuration(tabIndex: Int) {
        // Update the timer duration based on the selected tab index (Focus, Break, etc.)
        when (tabIndex) {
            0 -> { /* Set duration for Focus tab */}
            1 -> { /* Set duration for Break tab */}
            2 -> { /* Set duration for Rest tab */}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Pastikan untuk melepaskan media player saat fragment dihancurkan
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
