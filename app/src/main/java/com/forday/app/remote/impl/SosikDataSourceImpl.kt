package com.forday.app.remote.impl

import com.forday.app.data.model.SosikEntity
import com.forday.app.data.remote.SosikDataSource
import com.forday.app.remote.api.service.RoutineApi
import javax.inject.Inject

class SosikDataSourceImpl @Inject constructor(
    private val routineApi: RoutineApi
) : SosikDataSource {

    override suspend fun getPeopleRoutineList(
        hobbyId: Long?,
        lastRecordId: Long?,
        size: Long?,
        keyword: String?,
        storyFilterType: String?
    ): SosikEntity =
        routineApi.getPeopleRoutineList(
            hobbyId = hobbyId,
            lastRecordId = lastRecordId,
            size = size,
            keyword = keyword,
            storyFilterType = storyFilterType
        ).toData()
}
