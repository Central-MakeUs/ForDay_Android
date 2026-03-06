package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetAiRecommendedRoutinesAgainUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, type: String?) =
        hobbyRepository.getAiRecommendedRoutinesAgain(hobbyId, type)
}
