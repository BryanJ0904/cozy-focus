package com.example.cozyfocus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cozyfocus.model.Task

class TaskViewModel : ViewModel() {
    val task = MutableLiveData<Task>()
}