package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class CreateHobbyUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
){
    suspend operator fun invoke(
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        hobbyPeriod: Boolean
    ) = hobbyRepository.createHobby(
        selectedHobbyId = selectedHobbyId,
        selectedHobbyName = selectedHobbyName,
        selectedMinutes = selectedMinutes,
        selectedPurpose = selectedPurpose,
        selectedFrequency = selectedFrequency,
        hobbyPeriod = hobbyPeriod
    )
}