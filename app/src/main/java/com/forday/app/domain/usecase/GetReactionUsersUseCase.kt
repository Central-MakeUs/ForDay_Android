package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class GetReactionUsersUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Int, reactionType: String, lastUserId: String, size: Int) =
        routineRepository.getReactionUsers(recordId, reactionType, lastUserId, size)
}