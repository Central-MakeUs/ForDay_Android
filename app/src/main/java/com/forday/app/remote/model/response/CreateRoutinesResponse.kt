package com.forday.app.remote.model.response

import com.forday.app.data.model.CreateRoutinesDataEntity
import com.forday.app.data.model.CreateRoutinesEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

/**
 * 취미활동 추가에 대한 응답 Response
 * */

data class CreateRoutinesResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val isSuccess: Boolean,
    @SerializedName("data")
    val data: CreateRoutinesDataResponse
) : RemoteMapper<CreateRoutinesEntity> {
    override fun toData(): CreateRoutinesEntity {
        return CreateRoutinesEntity(
            status = status,
            isSuccess = isSuccess,
            data = data.toData()
        )
    }
}


data class CreateRoutinesDataResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("createdActivityNum")
    val createdRoutineNum: Int
) : RemoteMapper<CreateRoutinesDataEntity> {
    override fun toData(): CreateRoutinesDataEntity {
        return CreateRoutinesDataEntity(
            message = message,
            createdRoutineNum = createdRoutineNum,
        )
    }
}