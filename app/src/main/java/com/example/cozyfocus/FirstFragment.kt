package com.example.cozyfocus

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class FirstFragment : Fragment() {
    private var timerText: TextView? = null
    private var startButton: Button? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 1500000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_first, container, false)

        timerText = view.findViewById(R.id.timer_text)
        startButton = view.findViewById(R.id.button)

        startButton?.setOnClickListener { startTimer() }

        updateTimer()

        return view
    }

    private fun startTimer() {
        // Hentikan timer sebelumnya jika ada
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60

        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerText?.text = timeFormatted
    }
}
