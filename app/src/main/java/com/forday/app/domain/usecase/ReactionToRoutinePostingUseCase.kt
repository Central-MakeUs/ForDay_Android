package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class ReactionToRoutinePostingUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Int, reactionType: String) =
        routineRepository.reactionToRoutinePosting(recordId, reactionType)
}