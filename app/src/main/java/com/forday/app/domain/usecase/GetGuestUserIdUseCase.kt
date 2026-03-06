package com.forday.app.domain.usecase

import com.forday.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGuestUserIdUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<String?> =
        repository.getGuestUserId()
}
