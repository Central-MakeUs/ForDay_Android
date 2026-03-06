package com.forday.app.domain.usecase

import com.forday.app.domain.repository.UserRepository
import javax.inject.Inject

class SaveCreatedHobbyIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(hobbyId: Long) = repository.saveCreatedHobbyId(hobbyId)
}
