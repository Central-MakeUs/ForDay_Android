package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetSpecificRoutineListUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, size: Int?) =
        hobbyRepository.getSpecificRoutineList(hobbyId, size)
}