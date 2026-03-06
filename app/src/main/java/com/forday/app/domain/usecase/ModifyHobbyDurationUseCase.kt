package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ModifyHobbyDurationUseCase @Inject constructor(  // 취미 정보 수정 (취미 목표 기간)
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, goalDays: Boolean) =
        hobbyRepository.modifyHobbyDuration(hobbyId, goalDays)
}