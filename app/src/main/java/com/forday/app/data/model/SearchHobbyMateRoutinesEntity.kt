package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.RoutineItemDomain
import com.forday.app.domain.model.SearchHobbyMateRoutinesDataDomain
import com.forday.app.domain.model.SearchHobbyMateRoutinesDomain

data class SearchHobbyMateRoutinesEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: SearchHobbyMateRoutinesDataEntity
): DataMapper<SearchHobbyMateRoutinesDomain> {
    override fun toDomain(): SearchHobbyMateRoutinesDomain {
        return SearchHobbyMateRoutinesDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class SearchHobbyMateRoutinesDataEntity(
    val message: String,
    val activities: List<RoutineItemEntity>
): DataMapper<SearchHobbyMateRoutinesDataDomain> {
    override fun toDomain(): SearchHobbyMateRoutinesDataDomain {
        return SearchHobbyMateRoutinesDataDomain(
            message = message,
            activities = activities.map { it.toDomain() },
        )
    }
}

data class RoutineItemEntity(
    val id: Int,
    val content: String
): DataMapper<RoutineItemDomain> {
    override fun toDomain(): RoutineItemDomain {
        return RoutineItemDomain(
            routineId = id,
            content = content,
        )
    }
}