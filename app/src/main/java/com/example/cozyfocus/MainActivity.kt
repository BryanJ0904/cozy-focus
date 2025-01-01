package com.example.cozyfocus

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = true
    private lateinit var volumeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize and start media player for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        volumeButton = findViewById(R.id.volume_button)
        volumeButton.setOnClickListener {
            toggleVolume()
        }

        // Default fragment when activity is created
        if (savedInstanceState == null) {
            val firstFragment = FirstFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, firstFragment)
                .commit()
        }

        // Setup BottomNavigationView with logic for switching fragments
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.flFragment)
            if (currentFragment is StudyFragment && currentFragment.isTimerRunning) {
                currentFragment.showNavigationAlert(menuItem.itemId)
                false // Prevent immediate navigation
            }
            else{
                when (menuItem.itemId) {
                    R.id.home -> {
                        val firstFragment = FirstFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flFragment, firstFragment)
                            .commit()
                        if (!isPlaying) {
                            mediaPlayer.start()
                            isPlaying = true
                        }
                        true
                    }
                    R.id.task -> {
                        val secondFragment = SecondFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flFragment, secondFragment)
                            .commit()
                        stopMusic()
                        true
                    }
                    R.id.activity -> {
                        val thirdFragment = ThirdFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flFragment, thirdFragment)
                            .commit()
                        stopMusic()
                        true
                    }
                    R.id.profile -> {
                        val fourthFragment = FourthFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.flFragment, fourthFragment)
                            .commit()
                        stopMusic()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    fun navigateTo(menuItemId: Int) {
        val fragment = when (menuItemId) {
            R.id.task -> SecondFragment()
            R.id.activity -> ThirdFragment()
            R.id.profile -> FourthFragment()
            else -> null
        }

        fragment?.let {
            // Replace the fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, it)
                .commit()

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.setOnNavigationItemSelectedListener(null)
            bottomNavigationView.selectedItemId = menuItemId
            bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                val currentFragment = supportFragmentManager.findFragmentById(R.id.flFragment)
                if (currentFragment is StudyFragment && currentFragment.isTimerRunning) {
                    currentFragment.showNavigationAlert(menuItem.itemId)
                    false // Prevent immediate navigation
                }
                else{
                    when (menuItem.itemId) {
                        R.id.home -> {
                            val firstFragment = FirstFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.flFragment, firstFragment)
                                .commit()
                            if (!isPlaying) {
                                mediaPlayer.start()
                                isPlaying = true
                            }
                            true
                        }
                        R.id.task -> {
                            val secondFragment = SecondFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.flFragment, secondFragment)
                                .commit()
                            stopMusic()
                            true
                        }
                        R.id.activity -> {
                            val thirdFragment = ThirdFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.flFragment, thirdFragment)
                                .commit()
                            stopMusic()
                            true
                        }
                        R.id.profile -> {
                            val fourthFragment = FourthFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.flFragment, fourthFragment)
                                .commit()
                            stopMusic()
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    // Stop music when navigating to another fragment
    fun stopMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    // Toggle music volume (mute/unmute)
    private fun toggleVolume() {
        if (isPlaying) {
            mediaPlayer.setVolume(0f, 0f)
            volumeButton.setImageResource(R.drawable.nomusic)
        } else {
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.start()
            volumeButton.setImageResource(R.drawable.music)
        }
        isPlaying = !isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    // Override to handle fragment visibility
    override fun onResume() {
        super.onResume()
        // Start music when returning to activity
        if (!isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop music if the activity is paused
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }
}
