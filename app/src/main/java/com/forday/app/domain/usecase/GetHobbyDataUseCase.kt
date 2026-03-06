package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetHobbyDataUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke() = hobbyRepository.getHobbyCardData()
}