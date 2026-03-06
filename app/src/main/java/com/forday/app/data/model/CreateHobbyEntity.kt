package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.CreateHobbyDataDomain
import com.forday.app.domain.model.CreateHobbyDomain

data class CreateHobbyEntity(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateHobbyDataEntity
) : DataMapper<CreateHobbyDomain> {
    override fun toDomain(): CreateHobbyDomain {
        return CreateHobbyDomain(
            status = status,
            isSuccess = isSuccess,
            data = data.toDomain()
        )
    }

}

data class CreateHobbyDataEntity(
    val message: String,
    val hobbyId: Int
) : DataMapper<CreateHobbyDataDomain> {
    override fun toDomain(): CreateHobbyDataDomain {
        return CreateHobbyDataDomain(
            message = message,
            hobbyId = hobbyId,
        )
    }
}