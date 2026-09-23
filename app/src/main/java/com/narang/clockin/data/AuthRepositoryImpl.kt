package com.narang.clockin.data

import android.content.Context
import androidx.core.content.edit
import com.narang.clockin.domain.AuthRepository
import com.narang.clockin.domain.AuthUser
import com.narang.clockin.domain.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return try {
            Timber.d("Logging in via REST api")
            val user = api.login(LoginRequest(email, password)).toDomain(email)
            saveSession(user)
            Result.Success(user)
        } catch (e: IOException) {
            Timber.e(e, "Network error logging in")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            val msg = when (e.code()) {
                401 -> "Invalid email or password."
                else -> "Login failed: ${e.message()}"
            }
            Result.Error(msg, e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun signup(email: String, password: String): Result<AuthUser> {
        return try {
            Timber.d("Signing up via REST api")
            val user = api.signup(SignupRequest(email, password)).toDomain(email)
            saveSession(user)
            Result.Success(user)
        } catch (e: IOException) {
            Timber.e(e, "Network error signing up")
            Result.Error("No internet. Check your connection.", e)
        } catch (e: HttpException) {
            Timber.e(e, "Server error %d", e.code())
            val msg = when (e.code()) {
                409 -> "Account already exists. Please login."
                400 -> "Invalid email or password."
                else -> "Signup failed: ${e.message()}"
            }
            Result.Error(msg, e)
        } catch (e: Exception) {
            Timber.e(e, "Unknown error")
            Result.Error(e.message ?: "Unknown error", e)
        }
    }

    override fun logout() {
        prefs.edit { clear() }
        Timber.d("Session cleared")
    }

    override fun getCurrentUser(): AuthUser? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        return AuthUser(
            id = id,
            email = email,
            token = prefs.getString(KEY_TOKEN, null)
        )
    }

    private fun saveSession(user: AuthUser) {
        prefs.edit {
            putString(KEY_USER_ID, user.id)
            putString(KEY_EMAIL, user.email)
            putString(KEY_TOKEN, user.token)
        }
    }

    companion object {
        private const val PREFS_NAME = "auth_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN = "token"
    }
}
