package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class SetHobbyMainImageUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(hobbyId: Long?, coverImageUrl: String?, recordId: Long?) =
        hobbyRepository.setHobbyMainImage(hobbyId, coverImageUrl, recordId)


}