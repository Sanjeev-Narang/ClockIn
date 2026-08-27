package com.narang.clockin.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narang.clockin.data.model.Section
import com.narang.clockin.data.model.Task
import com.narang.clockin.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        if (userId.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid User ID. Please log in again.") }
            return
        }

        viewModelScope.launch {
            repository.observeTasks(userId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Unknown error occurred") }
                }
                .collect { tasks ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            highPriorityTasks = tasks.filter { t -> !t.isCompleted && t.section == Section.HIGH_PRIORITY },
                            upcomingTasks = tasks.filter { t -> !t.isCompleted && t.section == Section.UPCOMING }
                        )
                    }
                }
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(userId, task.id, isChecked)
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(userId, task.id)
        }
    }

    fun addNewTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(userId, task)
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(userId, task)
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}
