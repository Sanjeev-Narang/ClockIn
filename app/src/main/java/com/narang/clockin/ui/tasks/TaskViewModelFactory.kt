package com.narang.clockin.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.narang.clockin.data.repository.TaskRepository

/**
 * Simple factory for manual DI. If you adopt Hilt later, delete this file
 * and annotate TasksViewModel with @HiltViewModel + @Inject constructor
 * instead — nothing else in this module needs to change.
 */
class TaskViewModelFactory(
    private val repository: TaskRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
