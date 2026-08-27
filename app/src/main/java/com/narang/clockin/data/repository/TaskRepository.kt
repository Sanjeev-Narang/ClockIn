package com.narang.clockin.data.repository

import com.narang.clockin.data.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the data source. The ViewModel only ever talks to this
 * interface, never to Firestore directly — that's what keeps the module
 * swappable/testable (e.g. a FakeTaskRepository for unit tests).
 */
interface TaskRepository {
    fun observeTasks(userId: String): Flow<List<Task>>
    suspend fun setCompleted(userId: String, taskId: String, isCompleted: Boolean): Result<Unit>
    suspend fun addTask(userId: String, task: Task): Result<Unit>
    suspend fun updateTask(userId: String, task: Task): Result<Unit>
    suspend fun deleteTask(userId: String, taskId: String): Result<Unit>
}
