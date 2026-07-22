package com.jitz.adhoni_launcher.domain.repository

import com.jitz.adhoni_launcher.domain.Result

interface BiometricAuthRepository {
    suspend fun authenticate(): Result<Boolean>
    fun canAuthenticate(): Boolean
}
