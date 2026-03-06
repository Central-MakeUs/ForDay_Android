package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetMyHobbyListUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(inProgress: String?) =
        hobbyRepository.getMyHobbyList(inProgress)
}