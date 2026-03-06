package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ExtendHobbyPeriodUseCase @Inject constructor(  // 취미 연장
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, type: String) =
        hobbyRepository.extendHobbyPeriod(hobbyId, type)

}