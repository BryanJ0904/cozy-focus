package com.example.cozyfocus

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playPauseButton: ImageButton
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menambahkan FirstFragment ke dalam fragment container
        if (savedInstanceState == null) {
            val firstFragment = FirstFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, firstFragment)
                .commit()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer.isLooping = true

        playPauseButton = findViewById(R.id.volume_button)
        mediaPlayer.start()
        updateButtonIcon()
        playPauseButton.setOnClickListener {
            toggleMusic()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val firstFragment = FirstFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, firstFragment)
                        .commit()
                    true
                }
                R.id.task -> {
                    val secondFragment = SecondFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, secondFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun toggleMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        isPlaying = !isPlaying
        updateButtonIcon()
    }

    private fun updateButtonIcon() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.music)
        } else {
            playPauseButton.setImageResource(R.drawable.nomusic)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
