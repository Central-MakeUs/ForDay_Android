package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ModifyHobbyTimeUseCase @Inject constructor(  // 취미 정보 수정 (취미 시간)
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, minutes: Int) =
        hobbyRepository.modifyHobbyTime(hobbyId, minutes)
}