package com.narang.clockin.domain

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getTasks(userId: String): Result<List<Task>>
    suspend fun getTaskById(userId: String, id: String): Result<Task>
    fun getTasksFlow(userId: String): Flow<Result<List<Task>>>
    suspend fun setCompleted(userId: String, taskId: String, isCompleted: Boolean): Result<Unit>
    suspend fun addTask(userId: String, task: Task): Result<Unit>
    suspend fun updateTask(userId: String, task: Task): Result<Unit>
    suspend fun deleteTask(userId: String, taskId: String): Result<Unit>
}
