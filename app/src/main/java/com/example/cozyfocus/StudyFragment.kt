package com.example.cozyfocus

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class StudyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private var timer: CountDownTimer? = null
    private var currentTimerDuration = 10 * 1000L
    private var currentTabIndex = 0

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

        tabLayout.addTab(tabLayout.newTab().setText("Focus"))
        tabLayout.addTab(tabLayout.newTab().setText("Break"))
        tabLayout.addTab(tabLayout.newTab().setText("Rest"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                stopTimer()
                updateTimerDuration(10 * 1000L)
                startTimer(currentTimerDuration)
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
    }

    private fun updateTimerDuration(duration: Long) {
        currentTimerDuration = duration
        updateTimerText(currentTimerDuration)
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


    private fun updateTimerText(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        tvTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun moveToNextTab() {
        currentTabIndex = when (currentTabIndex) {
            0 -> 1
            1 -> 0
            0 -> 1
            1 -> 2
            else -> 0
        }

        val selectedTab = tabLayout.getTabAt(currentTabIndex)
        selectedTab?.select()
        updateTimerDuration(10 * 1000L)
    }
}
