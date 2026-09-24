package com.narang.clockin.data

import com.narang.clockin.domain.Result
import com.narang.clockin.domain.Task
import com.narang.clockin.domain.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApi
) : TaskRepository {

    override suspend fun getTasks(userId: String): Result<List<Task>> {
        return try {
            Timber.d("Fetching tasks from REST api")
            val dtos = api.getTasks(userId)
            val domain = dtos.map { it.toDomain() }
            Timber.d("Fetched %d tasks", domain.size)
            Result.Success(domain)
        } catch (e: IOException) {
            Timber.e(e, "Network error fetching tasks")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error("Unexpected error: ${e.message}", e)
        }
    }

    override suspend fun getTaskById(userId: String, id: String): Result<Task> {
        return try {
            val dto = api.getTaskById(id, userId)
            Result.Success(dto.toDomain())
        } catch (e: IOException) {
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override fun getTasksFlow(userId: String): Flow<Result<List<Task>>> = flow {
        emit(Result.Loading)
        emit(getTasks(userId))
    }

    override suspend fun setCompleted(userId: String, taskId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            Timber.d("Setting task %s completed: %s", taskId, isCompleted)
            api.setCompleted(taskId, SetCompletedRequest(userId, isCompleted))
            Result.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "Network error updating task")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun addTask(userId: String, task: Task): Result<Unit> {
        return try {
            Timber.d("Adding task: %s", task.title)
            api.createTask(task.toCreateRequest(userId))
            Result.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "Network error adding task")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun updateTask(userId: String, task: Task): Result<Unit> {
        return try {
            Timber.d("Updating task %s", task.id)
            api.updateTask(task.id, task.toUpdateRequest(userId))
            Result.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "Network error updating task")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            Timber.d("Deleting task %s", taskId)
            api.deleteTask(taskId, userId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "Network error deleting task")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            Result.Error(serverMessage(e.code(), e.message()), e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    private fun serverMessage(code: Int, detail: String?): String = when (code) {
        401 -> "Unauthorized. Please login again."
        404 -> "Task not found."
        in 500..599 -> "Server error. Try again later."
        else -> "Something went wrong: $detail"
    }
}
