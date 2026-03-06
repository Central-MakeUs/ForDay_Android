package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ModifyHobbyExecutionCountUseCase @Inject constructor(  // 취미 정보 수정 (취미 주당 횟수)
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, executionCount: Int) =
        hobbyRepository.modifyHobbyExecutionCount(hobbyId, executionCount)
}