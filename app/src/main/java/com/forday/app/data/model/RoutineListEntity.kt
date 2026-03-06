package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.RoutineListDataDomain
import com.forday.app.domain.model.RoutineListDomain
import com.forday.app.domain.model.RoutineListItemDomain
import kotlin.collections.map

data class RoutineListEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: RoutineListDataEntity
) : DataMapper<RoutineListDomain> {
    override fun toDomain(): RoutineListDomain = RoutineListDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class RoutineListDataEntity(
    val activities: List<RoutineListItemEntity>
) : DataMapper<RoutineListDataDomain> {
    override fun toDomain(): RoutineListDataDomain = RoutineListDataDomain(
        routines = activities.map { it.toDomain() }
    )
}

data class RoutineListItemEntity(
    val activityId: Int,
    val content: String,
    val aiRecommended: Boolean
) : DataMapper<RoutineListItemDomain> {
    override fun toDomain(): RoutineListItemDomain = RoutineListItemDomain(
        routineId = activityId,
        content = content,
        aiRecommended = aiRecommended
    )
}