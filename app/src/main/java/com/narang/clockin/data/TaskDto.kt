package com.narang.clockin.data

import com.narang.clockin.domain.Priority
import com.narang.clockin.domain.Task
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String?,
    @Json(name = "tag") val tag: String?,
    @Json(name = "priority") val priority: String?,
    @Json(name = "isCompleted") val isCompleted: Boolean?
)

@JsonClass(generateAdapter = true)
data class CreateTaskRequest(
    @Json(name = "userId") val userId: String,
    @Json(name = "title") val title: String,
    @Json(name = "tag") val tag: String,
    @Json(name = "priority") val priority: String,
    @Json(name = "isCompleted") val isCompleted: Boolean
)

@JsonClass(generateAdapter = true)
data class UpdateTaskRequest(
    @Json(name = "userId") val userId: String,
    @Json(name = "title") val title: String,
    @Json(name = "tag") val tag: String,
    @Json(name = "priority") val priority: String,
    @Json(name = "isCompleted") val isCompleted: Boolean
)

@JsonClass(generateAdapter = true)
data class SetCompletedRequest(
    @Json(name = "userId") val userId: String,
    @Json(name = "isCompleted") val isCompleted: Boolean
)

fun TaskDto.toDomain(): Task = Task(
    id = id,
    title = title ?: "",
    tag = tag ?: "",
    priority = priority ?: Priority.NORMAL.name,
    isCompleted = isCompleted ?: false
)

fun Task.toCreateRequest(userId: String): CreateTaskRequest = CreateTaskRequest(
    userId = userId,
    title = title,
    tag = tag,
    priority = priority,
    isCompleted = isCompleted
)

fun Task.toUpdateRequest(userId: String): UpdateTaskRequest = UpdateTaskRequest(
    userId = userId,
    title = title,
    tag = tag,
    priority = priority,
    isCompleted = isCompleted
)
