package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class ReportPostingUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Int, reason: String) =
        routineRepository.reportPosting(recordId, reason)
}
