package com.narang.clockin.data.repository

import com.narang.clockin.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirestoreTaskRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TaskRepository {

    private fun tasksRef(userId: String) =
        firestore.collection("users").document(userId).collection("tasks")

    override fun observeTasks(userId: String): Flow<List<Task>> = callbackFlow {
        Timber.d("Observing tasks for user: $userId")
        val registration = tasksRef(userId)
            .orderBy("dueDateTime")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error observing tasks for user: $userId")
                    close(error)
                    return@addSnapshotListener
                }
                
                val tasks = snapshot?.documents?.map { doc ->
                    Task.fromData(doc.data, doc.id)
                } ?: emptyList()
                
                Timber.d("Received ${tasks.size} tasks from Firestore")
                trySend(tasks)
            }
        awaitClose { 
            Timber.d("Closing task observer for user: $userId")
            registration.remove() 
        }
    }

    override suspend fun setCompleted(userId: String, taskId: String, isCompleted: Boolean): Result<Unit> =
        runCatching {
            Timber.d("Setting task $taskId completed: $isCompleted for user: $userId")
            tasksRef(userId).document(taskId).update("isCompleted", isCompleted).await()
            Unit
        }.onFailure { Timber.e(it, "Error setting task completed") }

    override suspend fun addTask(userId: String, task: Task): Result<Unit> =
        runCatching {
            Timber.d("Adding task: ${task.title} for user: $userId")
            tasksRef(userId).document().set(task.toMap()).await()
            Unit
        }.onSuccess { Timber.d("Task added successfully") }
        .onFailure { Timber.e(it, "Error adding task") }

    override suspend fun updateTask(userId: String, task: Task): Result<Unit> =
        runCatching {
            Timber.d("Updating task ${task.id}: ${task.title} for user: $userId")
            require(task.id.isNotEmpty()) { "Task id must not be empty for update" }
            tasksRef(userId).document(task.id).set(task.toMap()).await()
            Unit
        }.onSuccess { Timber.d("Task updated successfully") }
        .onFailure { Timber.e(it, "Error updating task") }

    override suspend fun deleteTask(userId: String, taskId: String): Result<Unit> =
        runCatching {
            Timber.d("Deleting task $taskId for user: $userId")
            tasksRef(userId).document(taskId).delete().await()
            Unit
        }.onFailure { Timber.e(it, "Error deleting task") }
}
