package com.forday.app.domain.repository

import com.forday.app.domain.model.SosikDomain

interface SosikRepository {
    suspend fun getPeopleRoutineList(
        hobbyId: Long?,
        lastRecordId: Long?,
        size: Long?,
        keyword: String?,
        storyFilterType: String?
    ): SosikDomain
}
