package com.forday.app.domain.usecase

import android.util.Log
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.repository.AuthRepository
import javax.inject.Inject

class GuestLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<GuestLoginDataDomain> = runCatching {
        authRepository.guestLogin().getOrThrow()
    }.onFailure { exception ->
        Log.e(
            TAG,
            """
            |Guest login failed
            |Exception type: ${exception::class.simpleName}
            |Message: ${exception.message}
            |Cause: ${exception.cause?.message}
            |Stack trace: ${exception.stackTraceToString()}
            """.trimMargin(),
            exception
        )
    }

    companion object {
        private const val TAG = "GuestLoginUseCase"
    }
}