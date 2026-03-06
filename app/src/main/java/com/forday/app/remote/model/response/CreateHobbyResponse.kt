package com.forday.app.remote.model.response

import com.forday.app.data.model.CreateHobbyDataEntity
import com.forday.app.data.model.CreateHobbyEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class CreateHobbyResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: CreateHobbyData
) : RemoteMapper<CreateHobbyEntity> {
    override fun toData(): CreateHobbyEntity {
        return CreateHobbyEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}


data class CreateHobbyData(
    @SerializedName("message")
    val message: String,
    @SerializedName("hobbyId")
    val hobbyId: Int
) : RemoteMapper<CreateHobbyDataEntity> {
    override fun toData(): CreateHobbyDataEntity {
        return CreateHobbyDataEntity(
            message = message,
            hobbyId = hobbyId,
        )
    }
}
