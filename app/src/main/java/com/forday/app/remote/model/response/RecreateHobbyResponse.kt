package com.forday.app.remote.model.response

import com.forday.app.data.model.RecreateHobbyDataEntity
import com.forday.app.data.model.RecreateHobbyEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class RecreateHobbyResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: RecreateHobbyData
) : RemoteMapper<RecreateHobbyEntity> {
    override fun toData(): RecreateHobbyEntity {
        return RecreateHobbyEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}

data class RecreateHobbyData(
    @SerializedName("hobbyId")
    val hobbyId: Long,
    @SerializedName("hobbyInfoId")
    val hobbyInfoId: Long,
    @SerializedName("hobbyName")
    val hobbyName: String,
    @SerializedName("hobbyPurpose")
    val hobbyPurpose: String,
    @SerializedName("hobbyTimeMinutes")
    val hobbyTimeMinutes: Int,
    @SerializedName("executionCount")
    val executionCount: Int,
    @SerializedName("goalDays")
    val goalDays: Int
) : RemoteMapper<RecreateHobbyDataEntity> {
    override fun toData(): RecreateHobbyDataEntity {
        return RecreateHobbyDataEntity(
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
