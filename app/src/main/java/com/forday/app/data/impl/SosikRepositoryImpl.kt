package com.forday.app.data.impl

import com.forday.app.data.remote.SosikDataSource
import com.forday.app.domain.model.SosikDomain
import com.forday.app.domain.repository.SosikRepository
import javax.inject.Inject

class SosikRepositoryImpl @Inject constructor(
    private val sosikDataSource: SosikDataSource
) : SosikRepository {

    override suspend fun getPeopleRoutineList(
        hobbyId: Long?,
        lastRecordId: Long?,
        size: Long?,
        keyword: String?,
        storyFilterType: String?
    ): SosikDomain =
        sosikDataSource.getPeopleRoutineList(
            hobbyId = hobbyId,
            lastRecordId = lastRecordId,
            size = size,
            keyword = keyword,
            storyFilterType = storyFilterType
        ).toDomain()
}
