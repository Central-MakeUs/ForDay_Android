package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class CancelMyReactionUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
){
    suspend operator fun invoke(routineId: Int, reactionType: String) =
        routineRepository.cancelMyReaction(routineId, reactionType)
}