package com.narang.clockin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narang.clockin.domain.AuthRepository
import com.narang.clockin.domain.Result
import com.narang.clockin.domain.Section
import com.narang.clockin.domain.Task
import com.narang.clockin.domain.TaskRepository
import com.narang.clockin.ui.tasks.TaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id.orEmpty()

    init {
        refresh()
    }

    fun refresh() {
        val uid = userId
        if (uid.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Invalid User ID. Please log in again.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            Timber.d("TaskViewModel: fetching tasks")
            when (val result = taskRepository.getTasks(uid)) {
                is Result.Success -> {
                    val tasks = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            highPriorityTasks = tasks.filter { t -> !t.isCompleted && t.section == Section.HIGH_PRIORITY },
                            upcomingTasks = tasks.filter { t -> !t.isCompleted && t.section == Section.UPCOMING }
                        )
                    }
                    Timber.d("Loaded %d tasks", tasks.size)
                }
                is Result.Error -> {
                    Timber.e("Error: %s", result.message)
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            when (val result = taskRepository.setCompleted(userId, task.id, isChecked)) {
                is Result.Success -> refresh()
                is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Result.Loading -> Unit
            }
        }
    }

    fun onDeleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(userId, task.id)
            refresh()
        }
    }

    fun addNewTask(task: Task) {
        viewModelScope.launch {
            when (val result = taskRepository.addTask(userId, task)) {
                is Result.Success -> refresh()
                is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Result.Loading -> Unit
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            when (val result = taskRepository.updateTask(userId, task)) {
                is Result.Success -> refresh()
                is Result.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                is Result.Loading -> Unit
            }
        }
    }
}
