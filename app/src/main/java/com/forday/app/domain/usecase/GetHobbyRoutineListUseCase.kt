package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetHobbyRoutineListUseCase @Inject constructor(   // 활동 리스트 조회
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?) =
        hobbyRepository.getHobbyRoutineList(hobbyId)
}