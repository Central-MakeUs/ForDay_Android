package com.forday.app.domain.usecase

import com.forday.app.domain.repository.RoutineRepository
import com.forday.app.remote.model.request.ModifyPostingRequest
import javax.inject.Inject

class ModifyPostingUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(
        recordId: Int,
        routineId: Int,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String
    ) = repository.modifyPosting(recordId, ModifyPostingRequest(routineId, sticker, memo, imageUrl, visibility))

}