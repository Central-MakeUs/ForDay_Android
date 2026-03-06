package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class GetUserFeedListUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?,
        userId: String? = null
    ) = routineRepository.getUserFeedList(hobbyIds, lastRecordId, feedSize, userId)
}