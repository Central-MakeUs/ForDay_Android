package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.*

data class HobbyRoutineListEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: HobbyRoutineListDataEntity
) : DataMapper<HobbyRoutineListDomain> {
    override fun toDomain(): HobbyRoutineListDomain = HobbyRoutineListDomain(
        status = status,
        isSuccess = isSuccess,
        data = data.toDomain()
    )
}

data class HobbyRoutineListDataEntity(
    val routines: List<RoutineDataEntity>,
    val errorClassName: String?,
    val message: String
) : DataMapper<HobbyRoutineListDataDomain> {
    override fun toDomain(): HobbyRoutineListDataDomain = HobbyRoutineListDataDomain(
        routines = routines.map { it.toDomain() },
        errorClassName = errorClassName,
        message = message
    )
}

data class RoutineDataEntity(
    val routineId: Int,
    val content: String,
    val aiRecommended: Boolean,
    val deletable: Boolean,
    val stickerCount: Int // 숫자로 변경됨에 따라 구조 조정
) : DataMapper<RoutineDataDomain> {
    override fun toDomain(): RoutineDataDomain = RoutineDataDomain(
        routineId = routineId,
        content = content,
        isAiRecommended = aiRecommended,
        isDeletable = deletable,
        stickerCount = stickerCount
    )
}