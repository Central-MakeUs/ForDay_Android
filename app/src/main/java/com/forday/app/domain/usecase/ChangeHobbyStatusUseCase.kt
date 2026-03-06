package com.forday.app.domain.usecase

import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class ChangeHobbyStatusUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
){
    suspend operator fun invoke(hobbyId: Long?, hobbyStatus: String) =
        hobbyRepository.changeHobbyStatus(hobbyId, hobbyStatus)
}