package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class DeletePostingUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Long) = repository.deletePosting(recordId)
}