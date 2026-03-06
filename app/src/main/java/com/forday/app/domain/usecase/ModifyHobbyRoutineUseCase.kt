package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ModifyHobbyRoutineUseCase @Inject constructor(  // 활동 수정하기
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(routineId: Long, content: String) =
        hobbyRepository.modifyHobbyRoutine(routineId, content)
}