package com.example.cozyfocus

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = true
    private lateinit var volumeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val firstFragment = FirstFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, firstFragment)
                .commit()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        volumeButton = findViewById(R.id.volume_button)
        volumeButton.setOnClickListener {
            toggleVolume()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
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
                R.id.profile -> {
                    val fourthFragment = FourthFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, fourthFragment)
                        .commit()
                    stopMusic()
                    true
                }
                // Menambahkan navigasi untuk item Activity
                R.id.activity -> {
                    val activityFragment = ThirdFragment() // Ganti dengan nama fragment yang sesuai
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, activityFragment)
                        .commit()
                    stopMusic()
                    true
                }
                else -> false
            }
        }
    }

    fun stopMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    fun toggleVolume() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.setVolume(0f, 0f)
            mediaPlayer.pause()
            volumeButton.setImageResource(R.drawable.nomusic)
            isPlaying = false
        } else {
            mediaPlayer.setVolume(1f, 1f)
            mediaPlayer.start()
            volumeButton.setImageResource(R.drawable.music)
            isPlaying = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
