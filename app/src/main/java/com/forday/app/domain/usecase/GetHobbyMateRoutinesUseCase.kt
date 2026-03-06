package com.forday.app.domain.usecase

import com.forday.app.domain.model.SearchHobbyMateRoutinesDomain
import com.forday.app.domain.repository.HobbyRepository
import javax.inject.Inject

class GetHobbyMateRoutinesUseCase @Inject constructor(
    private val hobbyRepository: HobbyRepository
) {
    suspend operator fun invoke(selectedHobbyId: Long?): SearchHobbyMateRoutinesDomain =
        hobbyRepository.searchHobbyMateRoutines(selectedHobbyId)
}