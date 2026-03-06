package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class RecreateHobbyUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(
        hobbyId: Long?,
        hobbyInfoId: Long?,
        hobbyName: String?,
        hobbyPurpose: String?,
        hobbyTimeMinutes: Int?,
        executionCount: Int?,
        durationSet: Boolean?
    ) = hobbyRepository.reCreateHobby(
        hobbyId = hobbyId,
        hobbyInfoId = hobbyInfoId,
        hobbyName = hobbyName,
        hobbyPurpose = hobbyPurpose,
        hobbyTimeMinutes = hobbyTimeMinutes,
        executionCount = executionCount,
        durationSet = durationSet
    )
}
