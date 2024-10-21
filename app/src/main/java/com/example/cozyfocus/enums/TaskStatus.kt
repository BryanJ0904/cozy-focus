package com.example.cozyfocus.enums

import com.example.cozyfocus.R

enum class TaskStatus(val value: Int) {
    NOT_STARTED(0),
    IN_PROGRESS(1),
    DONE(2);

    fun getDisplayName(): String {
        return when (this) {
            NOT_STARTED -> "Not Started"
            IN_PROGRESS -> "In Progress"
            DONE -> "Done"
        }
    }

    fun getStatusColor(status: Int): Int {
        return when (status) {
            0 -> R.color.info
            1 -> R.color.warning
            2 -> R.color.success
            else -> R.color.info
        }
    }

    companion object {
        fun fromValue(value: Int): TaskStatus {
            return when (value) {
                0 -> NOT_STARTED
                1 -> IN_PROGRESS
                2 -> DONE
                else -> NOT_STARTED
            }
        }
    }
}
