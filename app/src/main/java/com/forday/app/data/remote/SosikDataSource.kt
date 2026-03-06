package com.forday.app.data.remote

import com.forday.app.data.model.SosikEntity

interface SosikDataSource {
    suspend fun getPeopleRoutineList(
        hobbyId: Long?,
        lastRecordId: Long?,
        size: Long?,
        keyword: String?,
        storyFilterType: String?
    ): SosikEntity
}
