package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class CreateRoutinesUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, routineList: List<Pair<Boolean, String>>) =
        hobbyRepository.createRoutines(hobbyId, routineList)
}