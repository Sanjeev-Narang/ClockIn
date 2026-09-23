package com.narang.clockin.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApi {

    @GET("tasks")
    suspend fun getTasks(
        @Query("userId") userId: String
    ): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Path("id") id: String,
        @Query("userId") userId: String
    ): TaskDto

    @POST("tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): TaskDto

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body request: UpdateTaskRequest
    ): TaskDto

    @PATCH("tasks/{id}")
    suspend fun setCompleted(
        @Path("id") id: String,
        @Body request: SetCompletedRequest
    ): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String,
        @Query("userId") userId: String
    )
}
