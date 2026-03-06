package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class DeleteHobbyRoutineUseCase @Inject constructor(  // 활동 삭제하기
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(routineId: Long) =
        hobbyRepository.deleteHobbyRoutine(routineId)
}