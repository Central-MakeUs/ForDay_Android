package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class GetUserScrapListUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(lastScrapId: Long?, size: Long?, userId: String?) =
        routineRepository.getUserScrapList(lastScrapId, size, userId)
}