package com.forday.app.domain.usecase

import com.forday.app.domain.repository.SosikRepository
import javax.inject.Inject

class GetPeopleRoutineListUseCase @Inject constructor(
    private val sosikRepository: SosikRepository
) {
    suspend operator fun invoke(
        hobbyId: Long?,
        lastRecordId: Long?,
        size: Long?,
        keyword: String?,
        storyFilterType: String?
    ) = sosikRepository.getPeopleRoutineList(
        hobbyId = hobbyId,
        lastRecordId = lastRecordId,
        size = size,
        keyword = keyword,
        storyFilterType = storyFilterType
    )
}
