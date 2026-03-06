package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.RecreateHobbyDataDomain
import com.forday.app.domain.model.RecreateHobbyDomain

data class RecreateHobbyEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: RecreateHobbyDataEntity
) : DataMapper<RecreateHobbyDomain> {
    override fun toDomain(): RecreateHobbyDomain {
        return RecreateHobbyDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }
}

data class RecreateHobbyDataEntity(
    val hobbyId: Long,
    val hobbyInfoId: Long,
    val hobbyName: String,
    val hobbyPurpose: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int
) : DataMapper<RecreateHobbyDataDomain> {
    override fun toDomain(): RecreateHobbyDataDomain {
        return RecreateHobbyDataDomain(
            hobbyId = hobbyId,
            hobbyInfoId = hobbyInfoId,
            hobbyName = hobbyName,
            hobbyPurpose = hobbyPurpose,
            hobbyTimeMinutes = hobbyTimeMinutes,
            executionCount = executionCount,
            goalDays = goalDays
        )
    }
}
