package com.example.cozyfocus.model

import com.google.firebase.Timestamp

data class Task(
    val id: Int = 0,
    val title: String = "",
    val date: Timestamp = Timestamp.now(),
    val status: Int = 0
)
