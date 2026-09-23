package com.narang.clockin.ui.tasks

import com.narang.clockin.domain.Task

data class TaskUiState(
    val isLoading: Boolean = true,
    val highPriorityTasks: List<Task> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val errorMessage: String? = null
) {
    val totalHighPriorityCount: Int get() = highPriorityTasks.size
    val isEmpty: Boolean get() = !isLoading && highPriorityTasks.isEmpty() && upcomingTasks.isEmpty()
}
