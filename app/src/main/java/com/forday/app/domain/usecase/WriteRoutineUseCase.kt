package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class WriteRoutineUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String
    ) = hobbyRepository.writeRoutine(
        routineId = routineId,
        sticker = sticker,
        memo = memo,
        imageUrl = imageUrl,
        visibility = visibility
    )
}