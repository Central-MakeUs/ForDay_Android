package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import javax.inject.Inject

class ModifyPostingVisibilityUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend operator fun invoke(recordId: Int, visibility: String) =
        routineRepository.modifyPostingVisibility(recordId, visibility)
}