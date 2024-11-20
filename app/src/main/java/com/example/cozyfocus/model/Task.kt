package com.example.cozyfocus.model

import com.example.cozyfocus.enums.TaskStatus
import com.google.firebase.Timestamp

data class Task(
    val id: String = "",
    val title: String = "",
    val date: Timestamp = Timestamp.now(),
    val status: Int = TaskStatus.NOT_STARTED.ordinal
)
