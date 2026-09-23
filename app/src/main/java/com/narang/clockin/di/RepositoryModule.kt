package com.narang.clockin.di

import android.content.Context
import com.narang.clockin.data.AuthApi
import com.narang.clockin.data.AuthRepositoryImpl
import com.narang.clockin.data.TaskApi
import com.narang.clockin.data.TaskRepositoryImpl
import com.narang.clockin.domain.AuthRepository
import com.narang.clockin.domain.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(api: TaskApi): TaskRepository {
        return TaskRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(api, context)
    }
}
